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

package uk.ac.ebi.embl.api.validation.check.entries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.ValidationScope;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;
import uk.ac.ebi.embl.api.validation.annotation.GroupIncludeScope;

@Description("Entry Set has Duplicated entry_names \"{0}\"")
@ExcludeScope(validationScope={ValidationScope.ASSEMBLY_MASTER, ValidationScope.NCBI})
@GroupIncludeScope(group = { ValidationScope.Group.ASSEMBLY })
public class Entry_NameCheck extends EntriesValidationCheck
{
	protected final static String ENTRY_NAME_ID = "Entry_NameCheck1";

	@Override
	public ValidationResult check(ArrayList<Entry> entryList)
	{
		result = new ValidationResult();
		if(entryList==null)
		{
			return result;
		}
		Set<String> entry_nameSet = new HashSet<String>();
		for (Entry entry : entryList)
		{
			String entry_name = entry.getSubmitterAccession();
			if(entry_name==null)
			  continue;
			if (!entry_nameSet.add(entry_name))
			{
				reportError(entry.getOrigin(), ENTRY_NAME_ID, entry_name);
			}
		}

		return result;
	}

}
