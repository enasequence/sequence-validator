/*******************************************************************************
 * Copyright 2012 EMBL-EBI, Hinxton outstation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package uk.ac.ebi.embl.api.validation.check.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import uk.ac.ebi.embl.agp.reader.AGPFileReader;
import uk.ac.ebi.embl.agp.reader.AGPLineReader;
import uk.ac.ebi.embl.api.contant.AnalysisType;
import uk.ac.ebi.embl.api.entry.AgpRow;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.feature.SourceFeature;
import uk.ac.ebi.embl.api.entry.location.Location;
import uk.ac.ebi.embl.api.entry.location.LocationFactory;
import uk.ac.ebi.embl.api.entry.location.Order;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.helper.taxon.TaxonHelper;
import uk.ac.ebi.embl.api.validation.helper.taxon.TaxonHelperImpl;
import uk.ac.ebi.embl.api.validation.report.DefaultSubmissionReporter;
import uk.ac.ebi.embl.api.validation.report.SubmissionReporter;
import uk.ac.ebi.embl.api.validation.submission.Context;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType;

public abstract class FileValidationCheck {

	private SubmissionOptions options =null;
	protected SubmissionReporter reporter=null;
	private static final String REPORT_FILE_SUFFIX = ".report";
	protected static HashMap<String,List<Qualifier>> chromosomeNameQualifiers = new HashMap<String,List<Qualifier>>();
	protected static List<String> chromosomes =new ArrayList<String>();
	protected static List<String> contigs =new ArrayList<String>();
	protected static List<String> scaffolds =new ArrayList<String>();
	protected static List<String> agpEntryNames =new ArrayList<String>();
	protected static HashMap<String,AgpRow> contigRangeMap=new HashMap<String,AgpRow>();
	ArrayList<String> agpEntrynames = new ArrayList<String>();
	protected ConcurrentMap<String, AtomicLong> messageStats = null;
	protected static Entry masterEntry =null;
	protected TaxonHelper taxonHelper= null;
	private PrintWriter fixedFileWriter =null;
		   

	public FileValidationCheck(SubmissionOptions options) {
		this.options =options;
		messageStats =  new ConcurrentHashMap<String, AtomicLong>();
		taxonHelper =new TaxonHelperImpl();

	}
	public abstract boolean check(SubmissionFile file) throws ValidationEngineException;
	public boolean check() throws ValidationEngineException {
		return false;
	}

	protected SubmissionOptions getOptions() {
		return options;
	}

	protected AnalysisType getAnalysisType()
	{
		switch(getOptions().getEntryValidationPlanProperty().validationScope.get())
		{
		case ASSEMBLY_TRANSCRIPTOME:
			return AnalysisType.TRANSCRIPTOME_ASSEMBLY;
		case ASSEMBLY_CHROMOSOME:
		case ASSEMBLY_CONTIG:
		case ASSEMBLY_MASTER:
		case ASSEMBLY_SCAFFOLD:
			return AnalysisType.SEQUENCE_ASSEMBLY;
		default :
			return null;
		}
	}

	public SubmissionReporter getReporter()
	{
		HashSet<Severity> severity = new HashSet<Severity>();
		severity.add(Severity.ERROR);
		if(reporter==null)
			return new DefaultSubmissionReporter(severity);
		return reporter;
	}


	public  Path getReportFile(String reportDir, String fileName) throws ValidationEngineException
	{
		return Paths.get(reportDir, fileName + REPORT_FILE_SUFFIX );
	}

	public void readAGPfiles() throws ValidationEngineException
	{

		boolean valid = true;
		for( SubmissionFile submissionFile : options.submissionFiles.get().getFiles(FileType.AGP) ) 
		{
			try(BufferedReader fileReader= new BufferedReader(new FileReader(submissionFile.getFile())))
			{
				AGPFileReader reader = new AGPFileReader( new AGPLineReader(fileReader));

				ValidationResult vr = reader.read();
				int i=1;

				while(reader.isEntry() )
				{
					agpEntrynames.add( ( (Entry) reader.getEntry() ).getSubmitterAccession().toUpperCase() );

					for(AgpRow agpRow: ((Entry)reader.getEntry()).getSequence().getSortedAGPRows())
					{
						i++;
						if(!agpRow.isGap())
						{
							contigRangeMap.put(agpRow.getComponent_id().toUpperCase()+"_"+i,agpRow);
						}
					}
					vr = reader.read();
				}

			}catch(Exception e)
			{
				throw new ValidationEngineException(e.getMessage());
			}

		}

	}

	protected ValidationScope getValidationScope(String entryName)
	{
		if(options.context.get()==Context.genome)
			if(chromosomeNameQualifiers.get(entryName.toUpperCase())!=null)
			{
				chromosomes.add(entryName.toUpperCase());
				return ValidationScope.ASSEMBLY_CHROMOSOME;
			}
		if(agpEntrynames.contains(entryName.toUpperCase()))
		{
			scaffolds.add(entryName);
			return ValidationScope.ASSEMBLY_SCAFFOLD;
		}
		else
		{
			contigs.add(entryName.toUpperCase());
			return ValidationScope.ASSEMBLY_CONTIG;
		}
	}

	public void validateDuplicateEntryNames() throws ValidationEngineException
	{
		HashSet<String> entryNames = new HashSet<String>();
		List<String> duplicateEntryNames = new ArrayList<String>();
		for(String entryName:contigs)
		{
			if(!entryNames.add(entryName))
				duplicateEntryNames.add(entryName);
		}
		for(String entryName:scaffolds)
		{
			if(!entryNames.add(entryName));
			duplicateEntryNames.add(entryName);
		}
		for(String entryName:chromosomes)
		{
			if(!entryNames.add(entryName))
				duplicateEntryNames.add(entryName);
		}
		if(duplicateEntryNames.size()>0)
		{
			throw new ValidationEngineException("Entry names are duplicated in assembly : "+ String.join(",",duplicateEntryNames));
		}
	}

	public void validateSequencelessChromosomes() throws ValidationEngineException
	{
		List<String> sequencelessChromosomes = new ArrayList<String>();
		if(chromosomeNameQualifiers.size()!=chromosomes.size())
		{
			for(String chromosomeName: chromosomeNameQualifiers.keySet())
			{
				if(!chromosomes.contains(chromosomeName))
				{
					sequencelessChromosomes.add(chromosomeName);
				}
			}
			throw new ValidationEngineException("Sequenceless chromosomes are not allowed in assembly : "+String.join(",",sequencelessChromosomes));
		}
	}
	
	public String getDataclass(String entryName)
	{
		String dataclass=null;
		switch(getOptions().context.get())
		{
		case genome :
			switch(getOptions().getEntryValidationPlanProperty().fileType.get())
			{
			case FASTA:
			  switch(getOptions().getEntryValidationPlanProperty().validationScope.get())
				{
				case ASSEMBLY_CONTIG :
					dataclass= Entry.WGS_DATACLASS;
					break;
				case ASSEMBLY_CHROMOSOME :
					dataclass= Entry.STD_DATACLASS;
					break;
				default:
					break;
				}
			  break;
			case AGP:
				 dataclass= Entry.CON_DATACLASS;
				 break;
			case EMBL:
				 if(agpEntrynames.contains(entryName.toUpperCase()))
					 dataclass= Entry.CON_DATACLASS;
				 switch(getOptions().getEntryValidationPlanProperty().validationScope.get())
					{
					case ASSEMBLY_CONTIG :
						dataclass= Entry.WGS_DATACLASS;
						break;
					case ASSEMBLY_CHROMOSOME :
						dataclass= Entry.STD_DATACLASS;
						break;
					default:
						break;
					}
				 break;
			case MASTER :
				dataclass = Entry.SET_DATACLASS;
				break;
				  
			default:
				break;
				}
			break;
		case transcriptome:
			dataclass= Entry.TSA_DATACLASS;
			break;
		default:
			break;
		
		}
		return dataclass;
	}
	
	public ConcurrentMap<String,AtomicLong> getMessageStats()
	{
		return messageStats;
	}
	protected void addMessagekey(ValidationResult result)
	{
		for(ValidationMessage message: result.getMessages())
		{
		messageStats.putIfAbsent(message.getMessageKey(), new AtomicLong(1));
		messageStats.get(message.getMessageKey()).incrementAndGet();
		}
		
	}
     
 	protected void appendHeader(Entry entry) throws ValidationEngineException
	{
		if(masterEntry==null)
		{
			throw new ValidationEngineException("Master entry must to validate sequences");
		}
		if(entry==null)
			return ;
		entry.removeReferences();
		SourceFeature source=entry.getPrimarySourceFeature();
		entry.removeFeature(source);
		entry.addFeature(masterEntry.getPrimarySourceFeature());
		entry.addReferences(masterEntry.getReferences());
		entry.setDescription(masterEntry.getDescription());
		entry.addProjectAccessions(masterEntry.getProjectAccessions());
		entry.addXRefs(masterEntry.getXRefs());
		if(entry.getSequence()!=null)
		{
		entry.getSequence().setMoleculeType(entry.getPrimarySourceFeature().getSingleQualifierValue(Qualifier.MOL_TYPE_QUALIFIER_NAME));
		Order<Location>featureLocation = new Order<Location>();
		featureLocation.addLocation(new LocationFactory().createLocalRange(1l, entry.getSequence().getLength()));
		entry.getPrimarySourceFeature().setLocations(featureLocation);
		}
		//add chromosome qualifiers to entry
		if(entry.getSubmitterAccession()!=null)
		entry.getPrimarySourceFeature().addQualifiers(chromosomeNameQualifiers.get(entry.getSubmitterAccession().toUpperCase()));
		entry.setDataClass(getDataclass(entry.getSubmitterAccession()));
	}
 	
 	protected PrintWriter getFixedFileWriter(SubmissionFile submissionFile) throws FileNotFoundException
 	{
 		if(submissionFile.createFixedFile()&&fixedFileWriter==null)
 			fixedFileWriter= new PrintWriter(submissionFile.getFile().getAbsolutePath());
 		return fixedFileWriter;
 	}
}
