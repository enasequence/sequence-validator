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
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.StringUtils;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import uk.ac.ebi.embl.agp.reader.AGPFileReader;
import uk.ac.ebi.embl.agp.reader.AGPLineReader;
import uk.ac.ebi.embl.api.entry.AgpRow;
import uk.ac.ebi.embl.api.entry.AssemblySequenceInfo;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.sequence.SequenceFactory;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.helper.ByteBufferUtils;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlan;
import uk.ac.ebi.embl.api.validation.plan.ValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.Context;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType;
import uk.ac.ebi.embl.flatfile.validation.FlatFileValidations;
import uk.ac.ebi.embl.flatfile.writer.embl.EmblEntryWriter;

@Description("")
public class AGPFileValidationCheck extends FileValidationCheck
{

	private int i=0;
	public AGPFileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}	
	public boolean check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		boolean valid=true;
		ValidationPlan validationPlan =null;
		fixedFileWriter=null;
		Origin origin =null;
		try(BufferedReader fileReader= getBufferedReader(submissionFile.getFile());PrintWriter fixedFileWriter=getFixedFileWriter(submissionFile))
		{
			clearReportFile(getReportFile(submissionFile));
			if(!validateFileFormat(submissionFile.getFile(), uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType.AGP))
			{
				ValidationResult result = new ValidationResult();
				valid = false;
				result.append(FlatFileValidations.message(Severity.ERROR, "InvalidFileFormat","AGP"));
				if(getOptions().reportDir.isPresent())
				getReporter().writeToFile(getReportFile(submissionFile), result);
				addMessagekey(result);
				return valid;
			}
			i=0;
			AGPFileReader reader = new AGPFileReader(new AGPLineReader(fileReader));
			HashMap<String,AssemblySequenceInfo> contigInfo = new HashMap<>();
			contigInfo.putAll(AssemblySequenceInfo.getMapObject(options.processDir.get(), AssemblySequenceInfo.fastafileName));
			contigInfo.putAll(AssemblySequenceInfo.getMapObject(options.processDir.get(), AssemblySequenceInfo.flatfilefileName));
			if(contigInfo.isEmpty())
				throw new ValidationEngineException("AGP validation can't be done : Contig Info is missing");
			ValidationResult parseResult = reader.read();
			getOptions().getEntryValidationPlanProperty().fileType.set(uk.ac.ebi.embl.api.validation.FileType.AGP);
        	while(reader.isEntry())
        	{
        		if(!parseResult.isValid())
    			{	valid = false;
    				getReporter().writeToFile(getReportFile(submissionFile), parseResult);
    				addMessagekey(parseResult);
    			}
				parseResult=new ValidationResult();
        		Entry entry =reader.getEntry();
        		origin =entry.getOrigin();
    			getOptions().getEntryValidationPlanProperty().validationScope.set(getValidationScope(entry.getSubmitterAccession()));
    			getOptions().getEntryValidationPlanProperty().assemblySequenceInfo.set(contigInfo);
    			getOptions().getEntryValidationPlanProperty().sequenceNumber.set(getOptions().getEntryValidationPlanProperty().sequenceNumber.get()+1);
    			validationPlan = new EmblEntryValidationPlan(getOptions().getEntryValidationPlanProperty());
            	appendHeader(entry);
    			ValidationPlanResult planResult=validationPlan.execute(entry);
            	addEntryName(entry.getSubmitterAccession(),getOptions().getEntryValidationPlanProperty().validationScope.get(),entry.getSequence().getLength(),FileType.AGP);
    			if(!planResult.isValid())
    			{
    			    valid = false;
    				getReporter().writeToFile(getReportFile(submissionFile), planResult);
    				for(ValidationResult result: planResult.getResults())
    				{
    					addMessagekey(result);
    				}
    			}
    			else
				{
					if(fixedFileWriter!=null)
					new EmblEntryWriter(entry).write(fixedFileWriter);
					if(valid)
					constructAGPSequence(entry);
					//write AGP with sequence
					//entry.setNonExpandedCON(false);
					//new EmblEntryWriter(entry).write(sequenceFixedFileWriter);
				}
				reader.read();
        	}

		} catch (ValidationEngineException vee) {
			getReporter().writeToFile(getReportFile(submissionFile),Severity.ERROR, vee.getMessage(),origin);
			closeDB(getContigDB(), getSequenceDB());
			throw vee;
		}
		catch (Exception e) {
			getReporter().writeToFile(getReportFile(submissionFile),Severity.ERROR, e.getMessage(),origin);
			closeDB(getContigDB(), getSequenceDB());
			throw new ValidationEngineException(e.getMessage(), e);
		}
		if(valid)
	        registerAGPfileInfo();
		return valid;
	}
	
	private void constructAGPSequence(Entry entry) throws ValidationEngineException
    {
		try
		{
 		ByteBuffer sequenceBuffer=ByteBuffer.wrap(new byte[new Long(entry.getSequence().getLength()).intValue()]);
 
		ConcurrentMap contigMap =null;
		ConcurrentMap sequenceMap = null;
		if(getContigDB()!=null)
			contigMap=getContigDB().hashMap("map").createOrOpen();
		if(getSequenceDB()!=null)
		sequenceMap=getSequenceDB().hashMap("map").createOrOpen();


			for (AgpRow agpRow : entry.getSequence().getSortedAGPRows()) {
				i++;
				if (!agpRow.isGap()) {

					Object sequence;
					if (agpRow.getComponent_id() != null && getContigDB() != null) {

						Object rows = contigMap.get(agpRow.getComponent_id());
						if (rows != null) {
							for (AgpRow row : (List<AgpRow>) rows) {
								if (row.getObject().toLowerCase().equals(agpRow.getObject().toLowerCase())) {
									sequence = row.getSequence();

									if (sequence != null)
										sequenceBuffer.put((byte[]) sequence);
									else
										throw new ValidationEngineException("Failed to contruct AGP Sequence. invalid component:" + agpRow.getComponent_id());
								}
							}
						}
					}

				} else if (agpRow.getGap_length() != null)
					sequenceBuffer.put(StringUtils.repeat("N".toLowerCase(), agpRow.getGap_length().intValue()).getBytes());
			}
         entry.getSequence().setSequence(sequenceBuffer);
         if(getOptions().context.get()==Context.genome && getSequenceDB()!=null)
			{
        	 if(entry.getSubmitterAccession()!=null)
        	 {
				sequenceMap.put(entry.getSubmitterAccession().toUpperCase(),new String(entry.getSequence().getSequenceByte()));
			}
			}

		}catch(Exception e)
		{
			if(getSequenceDB()!=null)
				getSequenceDB().close();
			if(getContigDB()!=null)
				getContigDB().close();
			throw new ValidationEngineException(e);
		}
		if(getSequenceDB()!=null)
		getSequenceDB().commit();  
		}
	@Override
	public boolean check() throws ValidationEngineException {
		throw new UnsupportedOperationException();
	}
	
	public void getAGPEntries() throws ValidationEngineException
	{
		for( SubmissionFile submissionFile : options.submissionFiles.get().getFiles(FileType.AGP)) 
		{
			try(BufferedReader fileReader= getBufferedReader(submissionFile.getFile()))
			{
				AGPFileReader reader = new AGPFileReader( new AGPLineReader(fileReader));

				ValidationResult result=reader.read();
				int i=1;

				while(reader.isEntry())
				{
					if(result.isValid())
					{
						agpEntryNames.add((reader.getEntry()).getSubmitterAccession().toUpperCase());

						for (AgpRow agpRow : (reader.getEntry()).getSequence().getSortedAGPRows()) {
							if (!agpRow.isGap()) {
								if (agpRow.getComponent_id() != null && getContigDB() != null) {
									ConcurrentMap<String, Object> map = getContigDB().hashMap("map", Serializer.STRING, getContigDB().getDefaultSerializer()).createOrOpen();
									List<AgpRow> agpRows = (List<AgpRow>) map.get(agpRow.getComponent_id().toLowerCase());
									if (agpRows == null) {
										agpRows = new ArrayList<>();
									}
									agpRows.add(agpRow);
									map.put(agpRow.getComponent_id().toLowerCase(), agpRows);
								}
							}
							i++;
						}
					}
				result=reader.read();
				}

				if(getContigDB()!=null)
					getContigDB().commit();
			}catch(Exception e)
			{
				if(getContigDB()!=null)
					getContigDB().close();
				throw new ValidationEngineException(e);
			}

		}

	}
	private void registerAGPfileInfo() throws ValidationEngineException
	{
		AssemblySequenceInfo.writeMapObject(FileValidationCheck.agpInfo,options.processDir.get(),AssemblySequenceInfo.agpfileName);
	}
	
}
