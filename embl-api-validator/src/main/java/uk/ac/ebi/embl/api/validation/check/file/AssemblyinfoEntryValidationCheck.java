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

import java.nio.file.Paths;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.plan.GenomeAssemblyValidationPlan;
import uk.ac.ebi.embl.api.validation.plan.ValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;

@Description("")
public class AssemblyinfoEntryValidationCheck extends FileValidationCheck
{

	public AssemblyinfoEntryValidationCheck(SubmissionOptions options) {
		super(options);
	}

	@Override
	public boolean check() throws ValidationEngineException {

		if(options.isRemote)
			return true;
		ValidationPlan  validationPlan = new GenomeAssemblyValidationPlan(options.getEntryValidationPlanProperty());
		ValidationPlanResult validationPlanResult= validationPlan.execute(options.analysisId.get());
		getReporter().writeToFile(Paths.get(options.reportDir.get(),"LOAD_ASSEMBLY_INFO.report"), validationPlanResult);
		if(validationPlanResult.isValid())
			return true;
		return false;
	}

	@Override
	public boolean check(SubmissionFile file) throws ValidationEngineException {
		throw new UnsupportedOperationException();
	}

}