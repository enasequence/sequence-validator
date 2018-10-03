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
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.embl.agp.reader.AGPFileReader;
import uk.ac.ebi.embl.agp.reader.AGPLineReader;
import uk.ac.ebi.embl.api.entry.AgpRow;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlan;
import uk.ac.ebi.embl.api.validation.plan.ValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.flatfile.writer.embl.EmblEntryWriter;

@Description("")
public class AGPFileValidationCheck extends FileValidationCheck
{

	public AGPFileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}	
	public boolean check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		boolean valid=true;
		ValidationPlan validationPlan =null;
		try(BufferedReader fileReader= new BufferedReader(new FileReader(submissionFile.getFile()));PrintWriter fixedFileWriter=getFixedFileWriter(submissionFile))
		{
			AGPFileReader reader = new AGPFileReader(new AGPLineReader(fileReader));
			ValidationResult parseResult = reader.read();
			getOptions().getEntryValidationPlanProperty().fileType.set(FileType.AGP);
        	while(reader.isEntry())
        	{
        		if(!parseResult.isValid())
    			{	valid = false;
    				getReporter().writeToFile(getReportFile(getOptions().reportDir.get(), submissionFile.getFile().getName()), parseResult);
    				addMessagekey(parseResult);
    			}
        		Entry entry =reader.getEntry();
    			getOptions().getEntryValidationPlanProperty().validationScope.set(getValidationScope(entry.getSubmitterAccession()));
    			validationPlan = new EmblEntryValidationPlan(getOptions().getEntryValidationPlanProperty());
            	appendHeader(entry);
    			ValidationPlanResult planResult=validationPlan.execute(entry);
    			if(!planResult.isValid())
    			{
    			    valid = false;
    				if(getOptions().reportDir.isPresent())
    					getReporter().writeToFile(getReportFile(getOptions().reportDir.get(), submissionFile.getFile().getName()), planResult);
    				for(ValidationResult result: planResult.getResults())
    				{
    					addMessagekey(result);
    				}
    			}
    			else
				{
					if(fixedFileWriter!=null)
					new EmblEntryWriter(entry).write(getFixedFileWriter(submissionFile));
				}
				reader.read();
        	}

		}catch (Exception e) {
			throw new ValidationEngineException(e.getMessage());
		}
		return valid;
	}
	
	public void constructAGPSequence(Entry entry,ValidationResult result,String fileName) throws ValidationEngineException
    {
		int i=0;
		try
		{
 		ByteBuffer sequenceBuffer=ByteBuffer.wrap(new byte[new Long(entry.getSequence().getLength()).intValue()]);
 
         for(AgpRow agpRow: entry.getSequence().getSortedAGPRows())
         {
         	i++;
           	if(!agpRow.isGap())
         	{
           		sequenceBuffer.put(contigRangeMap.get(agpRow.getComponent_id().toUpperCase()+"_"+i).getSequence());
           		
         	}
           	else
           		sequenceBuffer.put(StringUtils.repeat("N".toLowerCase(), agpRow.getGap_length().intValue()).getBytes());           	
         }
         entry.getSequence().setSequence(sequenceBuffer);
		}catch(Exception e)
		{
			throw new ValidationEngineException(e.getMessage());
		}
    }
	@Override
	public boolean check() throws ValidationEngineException {
		throw new UnsupportedOperationException();
	}

}
