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
import uk.ac.ebi.embl.api.storage.DataRow;
import uk.ac.ebi.embl.api.storage.DataSet;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;
import uk.ac.ebi.embl.api.validation.helper.Utils;

@Description("One of qualifiers {0} must exist when qualifier {1} exists in any feature.")
@ExcludeScope( validationScope = {ValidationScope.NCBI, ValidationScope.NCBI_MASTER})
public class QualifierAndRequiredQualifierinEntryCheck extends EntryValidationCheck {

    private final static String SINGLE_MESSAGE_ID = "QualifierAndRequiredQualifierinEntryCheck";
    private final static String MULTIPLE_MESSAGE_ID = "QualifierAndRequiredQualifierinEntryCheck2";

    public QualifierAndRequiredQualifierinEntryCheck() {
    }

    public ValidationResult check(Entry entry) {
        result = new ValidationResult();
        DataSet dataSet = GlobalDataSets.getDataSet(FileName.QUALIFIER_REQUIRED_QUALIFIER_IN_ENTRY);

        if (entry == null) {
            return result;
        }

        for (DataRow dataRow : dataSet.getRows()) {
            String[] requiredQualifierNames = dataRow.getStringArray(0);
            String qualifierName = dataRow.getString(1);

            if (SequenceEntryUtils.isQualifierAvailable(qualifierName, entry)
                    && !SequenceEntryUtils.isAnyOfQualifiersAvailable(requiredQualifierNames, entry)) {
                String message = SINGLE_MESSAGE_ID;

                if (requiredQualifierNames.length > 1) {
                    message = MULTIPLE_MESSAGE_ID;
                }

               reportError(entry.getOrigin(), message,
                        Utils.paramArrayToString(requiredQualifierNames), qualifierName);
            }
        }

        return result;
    }

}
