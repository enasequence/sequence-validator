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

package uk.ac.ebi.embl.api.validation.cvtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class cv_fqual_value_fix_table
{
	ArrayList<cv_fqual_value_fix_record> cv_fqual_value_fix_records = new ArrayList<cv_fqual_value_fix_record>();
	Set<String> uniqueQualifierNames = new HashSet<String>();

	public void add(cv_fqual_value_fix_record record)
	{
		cv_fqual_value_fix_records.add(record);
	}

	public Set<String> getUniqueNames()
	{
		if (uniqueQualifierNames.size()!=0)
			return uniqueQualifierNames;

		for (cv_fqual_value_fix_record cv_fqual_value_fix_record : cv_fqual_value_fix_records)
		{
			uniqueQualifierNames.add(cv_fqual_value_fix_record.getFqualName());
		}

		return uniqueQualifierNames;
	}
	
	public HashMap<String, String> getQualifierValueMap(String qualifierName)
	{
		HashMap<String ,String> qualifierValueMap=new HashMap<String,String>();
		
		for(cv_fqual_value_fix_record cv_fqual_value_fix_record:cv_fqual_value_fix_records)
		{
			if(qualifierName.equals(cv_fqual_value_fix_record.getFqualName()))
			{
				qualifierValueMap.put(cv_fqual_value_fix_record.getRegex(), cv_fqual_value_fix_record.getValue());
			}
		}
		return qualifierValueMap;
		
	}
	
	public cv_fqual_value_fix_record create_cv_fqual_value_fix_record()

	{
		return new cv_fqual_value_fix_record();
	}
	public class cv_fqual_value_fix_record
	{
		private String fqualName;
		private String regex;
		private String value;

		public String getFqualName()
		{
			return fqualName;
		}

		public void setFqualName(String fqualName)
		{
			this.fqualName = fqualName;
		}

		public String getRegex()
		{
			return regex;
		}

		public void setRegex(String regex)
		{
			this.regex = regex;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}

	}

}
