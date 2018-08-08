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

import java.util.Arrays;
import java.util.List;

import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoAnalysisIdCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoCoverageCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoDuplicationCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoMinGapLengthCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoNameCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoPlatformCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoProgramCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoProjectIdheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoSamplewithDifferentProjectCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.AssemblyInfoSubmissionIdCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.ChromosomeListAnalysisIdCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.ChromosomeListChromosomeLocationCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.ChromosomeListChromosomeNameCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.ChromosomeListChromosomeTypeCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.ChromosomeListObjectNameCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.GenomeAssemblyValidationCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.UnlocalisedListChromosomeValidationCheck;
import uk.ac.ebi.embl.api.validation.check.genomeassembly.UnlocalisedListObjectNameValidationCheck;

@SuppressWarnings("unchecked")
public enum GenomeAssemblyValidationUnit
{
			ASSEMBLYINFO_CHECKS
			(
			AssemblyInfoAnalysisIdCheck.class,
			AssemblyInfoCoverageCheck.class,
			AssemblyInfoDuplicationCheck.class,
			AssemblyInfoMinGapLengthCheck.class,
			AssemblyInfoNameCheck.class,
			AssemblyInfoPlatformCheck.class,
			AssemblyInfoProgramCheck.class,
			AssemblyInfoProjectIdheck.class,
			AssemblyInfoSamplewithDifferentProjectCheck.class,
			AssemblyInfoSubmissionIdCheck.class
			),
			CHROMOSOME_LIST_CHECKS
			(
				ChromosomeListAnalysisIdCheck.class,
				ChromosomeListChromosomeLocationCheck.class,
				ChromosomeListChromosomeNameCheck.class,
				ChromosomeListChromosomeTypeCheck.class,
				ChromosomeListObjectNameCheck.class
			),
			UNLOCALISED_LIST_CHECKS
			(
				UnlocalisedListChromosomeValidationCheck.class,
				UnlocalisedListObjectNameValidationCheck.class
			);
				
			
			
	
	List<Class<? extends GenomeAssemblyValidationCheck<?>>> checks;

	GenomeAssemblyValidationUnit(Class<? extends GenomeAssemblyValidationCheck<?>>... checks)
	{
		this.checks = Arrays.asList(checks);
	}

	List<Class<? extends GenomeAssemblyValidationCheck<?>>> getValidationUnit()
	{
		return this.checks;
	}
}
