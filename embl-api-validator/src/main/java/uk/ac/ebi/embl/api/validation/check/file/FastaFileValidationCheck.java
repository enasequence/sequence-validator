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
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.fasta.reader.FastaFileReader;
import uk.ac.ebi.embl.fasta.reader.FastaLineReader;

@Description("")
public class FastaFileValidationCheck extends FileValidationCheck
{

	public FastaFileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}	
	public boolean check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		boolean valid=true;
		try(BufferedReader fileReader= new BufferedReader(new FileReader(submissionFile.getFile())))
		{
			FastaFileReader reader = new FastaFileReader( new FastaLineReader( fileReader));
			ValidationResult parseResult = reader.read();
			EmblEntryValidationPlan validationPlan =null;
			if(!parseResult.isValid())
			{
				valid = false;
				getReporter().writeToFile(getReportFile(getOptions().reportDir.get(), submissionFile.getFile().getName()), parseResult);
			}
			while(reader.isEntry())
			{
				Entry entry=reader.getEntry();
				List<String> contigKeys=contigRangeMap.entrySet().stream().filter(e -> e.getKey().contains(entry.getSubmitterAccession().toUpperCase())).map(e -> e.getKey()).collect(Collectors.toList());
            	for(String contigKey:contigKeys)
            	{
            		contigRangeMap.get(contigKey).setSequence(entry.getSequence().getSequenceByte(contigRangeMap.get(contigKey).getComponent_beg(),contigRangeMap.get(contigKey).getComponent_end()));
            	}
            	getOptions().getEntryValidationPlanProperty().validationScope.set(getValidationScope(entry.getSubmitterAccession().toUpperCase()));
            	validationPlan=new EmblEntryValidationPlan(getOptions().getEntryValidationPlanProperty());
				ValidationPlanResult result=validationPlan.execute(entry);
				getReporter().writeToFile(getReportFile(getOptions().reportDir.get(), submissionFile.getFile().getName()), result);
				reader.read();
			}
		}catch (Exception e) {
			throw new ValidationEngineException(e.getMessage());
		}
		return valid;	
	}

}