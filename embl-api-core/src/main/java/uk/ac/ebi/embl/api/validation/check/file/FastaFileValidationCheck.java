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

import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;

@Description("")
public class FastaFileValidationCheck extends FileValidationCheck
{
	@Override
	public ValidationResult check(SubmissionFiles files) throws ValidationEngineException
	{
        for(SubmissionFile submissionFile : files.getFiles(SubmissionFile.FileType.FASTA))
        {
        	FastaFileReader reader = new FastaFileReader( new FastaLineReader( AssemblyFileUtils.getBufferedReader( submissionFile.getFile() )));
            ValidationResult parseResult = reader.read();
            if(!parseResult.isValid())
            {
            	valid = false;
        }
		return null;
	}
	
}
