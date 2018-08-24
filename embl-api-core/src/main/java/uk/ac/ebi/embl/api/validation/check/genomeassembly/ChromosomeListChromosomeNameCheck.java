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
package uk.ac.ebi.embl.api.validation.check.genomeassembly;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.embl.api.entry.genomeassembly.ChromosomeEntry;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.helper.taxon.TaxonHelper;
import uk.ac.ebi.embl.api.validation.helper.taxon.TaxonHelperImpl;

@Description("")
public class ChromosomeListChromosomeNameCheck extends GenomeAssemblyValidationCheck<ChromosomeEntry>
{
	private final String MESSAGE_KEY_MISSING_CHROMOSOME_NAME_ERROR = "ChromosomeListMissingNameCheck";
	private final String MESSAGE_KEY_CHROMOSOME_NAME_LENGTH_ERROR = "ChromosomeListNameLengthCheck";
	private final String MESSAGE_KEY_CHROMOSOME_NAME_REGEX_ERROR = "ChromosomeListNameRegexCheck";
	private final String MESSAGE_KEY_INVALID_CHROMOSOME_NAME_ERROR = "ChromosomeListNameInvalidCheck";
	
	Pattern ChromosomeNamePattern = Pattern.compile("^([A-Za-z0-9]){1}([A-Za-z0-9_\\.]|-)*$");
	String[] invalidChromosomeNameArray = new String[] { "chr", "chrm", "chrom", "chromosome", "linkage group", "linkage-group", "plasmid"}; 
    	

	public ValidationResult check(ChromosomeEntry entry) throws ValidationEngineException
	{
          if(entry==null)
        	  return result;
          TaxonHelper taxonHelper = new TaxonHelperImpl();
	
		if (null == entry.getChromosomeName())
		{
			reportError(entry.getOrigin(), MESSAGE_KEY_MISSING_CHROMOSOME_NAME_ERROR, entry.getObjectName());
			return result;
		}
		if(entry.getChromosomeName().length()>=33)
		{
			reportError(entry.getOrigin(), MESSAGE_KEY_CHROMOSOME_NAME_LENGTH_ERROR, entry.getObjectName());
		}
		if(!ChromosomeNamePattern.matcher(entry.getChromosomeName().trim()).matches())
		{
			reportError(entry.getOrigin(), MESSAGE_KEY_CHROMOSOME_NAME_REGEX_ERROR, entry.getObjectName());
		}
        Arrays.stream(invalidChromosomeNameArray).filter(x->StringUtils.containsIgnoreCase(entry.getChromosomeName(),x)).forEach(x->
        {
    	    entry.setChromosomeName(StringUtils.remove(entry.getChromosomeName(),entry.getChromosomeName().substring(StringUtils.indexOfIgnoreCase(entry.getChromosomeName(),x),StringUtils.indexOfIgnoreCase(entry.getChromosomeName(),x)+x.length())));
        });
        if(entry.getChromosomeName().trim().equalsIgnoreCase("Mitocondria"))
        {
           entry.setChromosomeName("MT");	
        }
		return result;				
	}	

}
