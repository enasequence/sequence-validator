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

import uk.ac.ebi.embl.api.entry.AssemblySequenceInfo;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.sequence.Sequence;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.fixer.entry.EntryNameFix;
import uk.ac.ebi.embl.api.validation.helper.ByteBufferUtils;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.Context;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.common.CommonUtil;
import uk.ac.ebi.embl.fasta.reader.FastaFileReader;
import uk.ac.ebi.embl.fasta.reader.FastaLineReader;
import uk.ac.ebi.embl.flatfile.writer.embl.EmblEntryWriter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentMap;

@Description("")
public class FastaFileValidationCheck extends FileValidationCheck
{

	public FastaFileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public ValidationResult check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		ValidationResult validationResult = new ValidationResult();
		fixedFileWriter =null;
		ConcurrentMap sequenceMap =null;
		Origin origin =null;
		if(getSequenceDB()!=null)
			sequenceMap= getSequenceDB().hashMap("map").createOrOpen();

		try(BufferedReader fileReader= CommonUtil.bufferedReaderFromFile(submissionFile.getFile()); PrintWriter fixedFileWriter=getFixedFileWriter(submissionFile))
		{
			clearReportFile(getReportFile(submissionFile));

			if(!validateFileFormat(submissionFile.getFile(), uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType.FASTA))
			{
				addErrorAndReport(validationResult,submissionFile, "InvalidFileFormat","flatfile");
				return validationResult;
			}
			FastaFileReader reader = new FastaFileReader( new FastaLineReader( fileReader));
			ValidationResult parseResult = reader.read();
			validationResult.append(parseResult);
			EmblEntryValidationPlan validationPlan;
		
			while(reader.isEntry())
			{
				if(!parseResult.isValid())
				{
					getReporter().writeToFile(getReportFile(submissionFile), parseResult);
					addMessageStats(parseResult.getMessages());
				}

				Entry entry=reader.getEntry();
				origin=entry.getOrigin();
				entry.setSubmitterAccession(EntryNameFix.getFixedEntryName(entry.getSubmitterAccession()));
				if(getOptions().context.get()==Context.genome)
				{
					if (entry.getSubmitterAccession() == null)
						entry.setSubmitterAccession(entry.getPrimaryAccession());
	    			getOptions().getEntryValidationPlanProperty().sequenceNumber.set(getOptions().getEntryValidationPlanProperty().sequenceNumber.get()+1);
					if(isHasAnnotationOnlyFlatfile()) {
						collectContigInfo(entry);
						if (entry.getSubmitterAccession() != null && getSequenceDB() != null) {
							sequenceMap.put(entry.getSubmitterAccession().toUpperCase(), ByteBufferUtils.string(entry.getSequence().getSequenceBuffer()));
						}
					}
				}
            	getOptions().getEntryValidationPlanProperty().validationScope.set(getValidationScope(entry.getSubmitterAccession()));

				Sequence.Topology chrListToplogy = getTopology(entry.getSubmitterAccession());
				if (chrListToplogy != null) {
					entry.getSequence().setTopology(chrListToplogy);
				}
            	getOptions().getEntryValidationPlanProperty().fileType.set(uk.ac.ebi.embl.api.validation.FileType.FASTA);
            	validationPlan=new EmblEntryValidationPlan(getOptions().getEntryValidationPlanProperty());
            	appendHeader(entry);
				ValidationResult planResult=validationPlan.execute(entry);
				validationResult.append(planResult);

				if(null != entry.getSubmitterAccession()) {
					addEntryName(entry.getSubmitterAccession());
					int assemblyLevel = getAssemblyLevel(getOptions().getEntryValidationPlanProperty().validationScope.get());
					AssemblySequenceInfo sequenceInfo = new AssemblySequenceInfo(entry.getSequence().getLength(), assemblyLevel, null);
					FileValidationCheck.fastaInfo.put(entry.getSubmitterAccession().toUpperCase(), sequenceInfo);
				}

				if(!planResult.isValid())
				{
    				getReporter().writeToFile(getReportFile(submissionFile), planResult);
					addMessageStats(planResult.getMessages());
				}
				else
				{
					if(fixedFileWriter!=null)
					new EmblEntryWriter(entry).write(getFixedFileWriter(submissionFile));
				}
				parseResult= reader.read();
				validationResult.append(planResult);
				sequenceCount++;
			}
			if(getSequenceDB()!=null)
			{
				getSequenceDB().commit();
			}
			if(getContigDB()!=null)
			{
				getContigDB().commit();
			}
		} catch (ValidationEngineException e) {
			getReporter().writeToFile(getReportFile(submissionFile),Severity.ERROR, e.getMessage(),origin);
			closeDB(getSequenceDB(), getContigDB());
			throw e;
		}
		catch (Exception e) {
			getReporter().writeToFile(getReportFile(submissionFile),Severity.ERROR, e.getMessage(),origin);
			closeDB(getSequenceDB(), getContigDB());
			throw new ValidationEngineException(e.getMessage(), e);
		}

		if(validationResult.isValid())
			registerFastaInfo();
		return validationResult;
	}
	private void registerFastaInfo() throws ValidationEngineException
	{
		AssemblySequenceInfo.writeMapObject(FileValidationCheck.fastaInfo,options.processDir.get(),AssemblySequenceInfo.fastafileName);
	}

	@Override
	public ValidationResult check() throws ValidationEngineException {
		throw new UnsupportedOperationException();
	}
	
}
