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
import uk.ac.ebi.embl.api.validation.EntryValidations;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;
import uk.ac.ebi.embl.api.validation.annotation.Description;

import java.util.Calendar;
import java.util.Date;

@Description("The hold date must not be in the past")
public class HoldDateCheck extends EntryValidationCheck {

    private final static String MESSAGE_ID = "HoldDateCheck";

    public ValidationResult check(Entry entry) {
        result = new ValidationResult();
        
        if (entry != null) {
            Date holdDate = entry.getHoldDate();
            if (holdDate != null) {
                Date now = Calendar.getInstance().getTime();
                if (holdDate.compareTo(now) < 0) {//hold date before now
                    result.append(EntryValidations.createMessage(entry.getOrigin(), Severity.ERROR, MESSAGE_ID));
                }
            }
        }
        return result;
    }
}
