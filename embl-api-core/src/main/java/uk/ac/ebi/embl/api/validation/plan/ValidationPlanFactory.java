/*
 * Copyright 2018 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.ac.ebi.embl.api.validation.plan;

import java.sql.Connection;

import uk.ac.ebi.embl.api.validation.FileType;
import uk.ac.ebi.embl.api.validation.ValidationMessageManager;
import uk.ac.ebi.embl.api.validation.ValidationScope;

public class ValidationPlanFactory
{
	public static ValidationPlan getValidationPlan(FileType fileType, ValidationScope scope, Connection con,int min_gap_length,boolean assembly)
	{
		return getValidationPlan(fileType, scope, false, false, con, min_gap_length,assembly);		
	}

	public static ValidationPlan getValidationPlan(FileType fileType, ValidationScope scope, boolean fixMode, boolean devMode, Connection con,
			int min_gap_length,boolean assembly)
	{//ValidationScope validationScope, boolean devMode, boolean fix,Connection con,int min_gap_length,boolean assembly
		ValidationPlan validationPlan = null;
		EmblEntryValidationPlanProperty emblEntryValidationProperty = new EmblEntryValidationPlanProperty();
		emblEntryValidationProperty.validationScope.set(scope);
		emblEntryValidationProperty.isDevMode.set(devMode);
		emblEntryValidationProperty.isFixMode.set(fixMode);
		emblEntryValidationProperty.enproConnection.set(con);
		emblEntryValidationProperty.minGapLength.set(min_gap_length);
		emblEntryValidationProperty.isAssembly.set(assembly);
		emblEntryValidationProperty.fileType.set(fileType);
		
		switch (fileType)
		{
			case EMBL:
			case GENBANK:
		{

			validationPlan = new EmblEntryValidationPlan(emblEntryValidationProperty);
			validationPlan.addMessageBundle(ValidationMessageManager.STANDARD_VALIDATION_BUNDLE);
			validationPlan.addMessageBundle(ValidationMessageManager.STANDARD_FIXER_BUNDLE);
			break;
		}
			case GFF3:
			{
				validationPlan = new GFF3ValidationPlan(emblEntryValidationProperty);
				validationPlan.addMessageBundle(ValidationMessageManager.GFF3_VALIDATION_BUNDLE);
				break;
			}
			case ASSEMBLYINFO:
			case CHROMOSOMELIST:
			case UNLOCALISEDLIST:
			{
				validationPlan = new GenomeAssemblyValidationPlan(emblEntryValidationProperty);
				validationPlan.addMessageBundle(ValidationMessageManager.GENOMEASSEMBLY_VALIDATION_BUNDLE);
				validationPlan.addMessageBundle(ValidationMessageManager.GENOMEASSEMBLY_FIXER_BUNDLE);
				break;
			}
		default:
			break;
				
		}
		return validationPlan;
	}
}
