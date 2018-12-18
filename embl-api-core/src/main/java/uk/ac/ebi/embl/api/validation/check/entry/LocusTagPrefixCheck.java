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
package uk.ac.ebi.embl.api.validation.check.entry;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.Text;
import uk.ac.ebi.embl.api.entry.XRef;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;

import java.sql.SQLException;
import java.util.*;

@Description("Illegal /locus_tag value \"{0} \". locus_tag prefix \"{1}\" is not registered with the project")
@ExcludeScope(validationScope = {ValidationScope.NCBI})
public class LocusTagPrefixCheck extends EntryValidationCheck {

	protected final static String MESSAGE_ID_INVALID_PREFIX = "LocusTagPrefixCheck1";


   public ValidationResult check(Entry entry) throws ValidationEngineException {
        result = new ValidationResult();
        HashSet<String> projectLocustagPrefixes=new HashSet<String>();
        List<Text> projectAccessions=new ArrayList<Text>();
        String samplePrefix=null;

        if (entry == null) {
            return result;
        }

      try
      { 
    	  List<Qualifier> locusTagQualifiers=SequenceEntryUtils.getQualifiers(Qualifier.LOCUS_TAG_QUALIFIER_NAME, entry);
           
       if(locusTagQualifiers.size()==0||(getEntryDAOUtils()==null&&getEmblEntryValidationPlanProperty().locus_tag_prefixes.get().size()==0))
       {
    	   return result;
       }
       
       if(entry.getProjectAccessions()!=null&&entry.getProjectAccessions().size()!=0)
       {
    	   projectAccessions.addAll(entry.getProjectAccessions());
       }
       if(entry.getXRefs().size()!=0)
       {
            List<XRef> xrefs= entry.getXRefs();
    	   
    	   for(XRef xref:xrefs)
    	   {
    		   if("BioSample".equals(xref.getDatabase()))
    		   {
    			   samplePrefix=xref.getPrimaryAccession();
    		   }
    	   }
        }
    	
       if(getEmblEntryValidationPlanProperty().locus_tag_prefixes.get().size()!=0)
       {
    	   projectLocustagPrefixes.addAll(getEmblEntryValidationPlanProperty().locus_tag_prefixes.get());
       }
       else
       {
				for (Text projectAccession : projectAccessions) 
				{
					HashSet<String> locusTagPrefixes = getEntryDAOUtils().getProjectLocutagPrefix(projectAccession.getText());
					if (!locusTagPrefixes.isEmpty())
						projectLocustagPrefixes.addAll(locusTagPrefixes);
				}
       }
       
       for(Qualifier qualifier:locusTagQualifiers)
       {
    	   String locusTagValue=qualifier.getValue();
    	   
    	   if(locusTagValue!=null)
    	   {
    		   String locustagPrefix=locusTagValue.split("_")[0];
    		   
    		   if(samplePrefix!=null&&locustagPrefix.equals(samplePrefix))
    		   {
    			   continue;
    		   }
    		   if(projectAccessions.size()!=0&&!projectLocustagPrefixes.contains(locustagPrefix))
    		   {
    			  reportError(qualifier.getOrigin(), MESSAGE_ID_INVALID_PREFIX,locusTagValue,locustagPrefix );
    		   }
    	   }
       }

        return result;
    }catch(SQLException e)
      {
    	throw new ValidationEngineException();
      }
   }

    
}
