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

import java.util.List;
import uk.ac.ebi.embl.api.entry.genomeassembly.UnlocalisedEntry;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.plan.GenomeAssemblyValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.flatfile.reader.genomeassembly.UnlocalisedListFileReader;

@Description("")
public class UnlocalisedListFileValidationCheck extends FileValidationCheck
{

	public UnlocalisedListFileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}	
	@Override
	public boolean check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		boolean valid =true;
		try
		{
			clearReportFile(getReportFile(submissionFile));
			UnlocalisedListFileReader reader = new UnlocalisedListFileReader(submissionFile.getFile());
			ValidationResult parseResult = reader.read();
			if(!parseResult.isValid())
			{
				valid = false;
				getReporter().writeToFile(getReportFile(submissionFile), parseResult);
				addMessagekey(parseResult);
			}
			getOptions().getEntryValidationPlanProperty().fileType.set(FileType.UNLOCALISEDLIST);
			GenomeAssemblyValidationPlan plan = new GenomeAssemblyValidationPlan(getOptions().getEntryValidationPlanProperty());
			List<UnlocalisedEntry> unlocalisedEntries=reader.getentries();
			for(UnlocalisedEntry entry : unlocalisedEntries)
			{
				ValidationPlanResult result=plan.execute(entry);
				result.append(validateValidChromosomeEntry(entry));
				result.append(validateValidUnlocalisedEntry(entry));
				if(!result.isValid())
				{
				valid=false;
				getReporter().writeToFile(getReportFile(submissionFile), result);
				for(ValidationResult planResult: result.getResults())
				{
					addMessagekey(planResult);
				}
				}
			}
		}catch(Exception e)
		{
			throw new ValidationEngineException(e.getMessage());
		}
		return valid;
	}
	@Override
	public boolean check() throws ValidationEngineException {
		throw new UnsupportedOperationException();
	}


	ValidationResult validateValidChromosomeEntry(UnlocalisedEntry unlocalisedEntry)
	{
		ValidationResult result = new ValidationResult();
		if(unlocalisedEntry.getChromosomeName()!=null)
		{
			if(chromosomeNames.size()!=0&&chromosomeNames.contains(unlocalisedEntry.getChromosomeName().toUpperCase()))
			{
				ValidationMessage message = new ValidationMessage<>(Severity.ERROR, "UnlocalisedListChromosomeValidCheck",unlocalisedEntry.getChromosomeName());
				result.append(message);
			}
		}
		return result;
	}
	
	ValidationResult validateValidUnlocalisedEntry(UnlocalisedEntry unlocalisedEntry)
	{
		ValidationResult result = new ValidationResult();
		if(unlocalisedEntry.getObjectName()!=null)
		{
			if(entryNames.size()!=0&&!entryNames.contains(unlocalisedEntry.getObjectName().toUpperCase()))
			{
				ValidationMessage message = new ValidationMessage<>(Severity.ERROR, "UnlocalisedListUnlocalisedValidCheck",unlocalisedEntry.getObjectName());
				result.append(message);
			}
		}
		return result;
	}
}
