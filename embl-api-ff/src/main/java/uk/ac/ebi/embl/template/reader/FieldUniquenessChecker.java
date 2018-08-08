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

package uk.ac.ebi.embl.template.reader;

import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.embl.api.validation.ValidationResult;

import org.apache.log4j.Logger;

import java.util.*;

public class FieldUniquenessChecker {
    private static final Logger LOGGER = Logger.getLogger(FieldUniquenessChecker.class);

    public void check(TemplateVariablesSet variables, List<TemplateProcessorResultSet> results) {
        /**
         * stick the results in keyed by entry number for easy access
         */
        Map<Integer, TemplateProcessorResultSet> resultsMap = new HashMap<Integer, TemplateProcessorResultSet>();
        for (TemplateProcessorResultSet result : results) {
            resultsMap.put(result.getEntryNumber(), result);
        }

        HashMap<String, Integer> stringsMap = new HashMap<String, Integer>();
        ArrayList<Integer> variableKeys = new ArrayList<Integer>(variables.getEntryNumbers());

        int count = 0;

        /**
         * then make concatenated strings of constants and variables (minus the sequence)
         */
        Collections.sort(variableKeys);
        for (int entryNumber : variableKeys) {

            count++;

            TemplateVariables entryVariables = variables.getEntryValues(entryNumber);
            StringBuilder variablesBuilder = new StringBuilder();
            ArrayList<String> tokenNames = new ArrayList<String>(entryVariables.getTokenNames());
            Collections.sort(tokenNames);//want to ensure a consistent order so the strings will be meaningfully comparable
            for (String tokenName : tokenNames) {
                if (!tokenName.equals(TemplateProcessorConstants.SEQUENCE_TOKEN) && !tokenName.equals(TemplateProcessorConstants.SEQUENCE_LENGTH_TOKEN)) {//dont want to include the sequence
                    String tokenValue = entryVariables.getTokenValue(tokenName);
                    variablesBuilder.append(tokenValue);
                }
            }

            String totalString = variablesBuilder.toString();

            if (stringsMap.containsKey(totalString)) {//there is a duplicate
                TemplateProcessorResultSet resultsSet = resultsMap.get(entryNumber);
                if (resultsSet != null) {
                    Integer duplicateEntryNumber = stringsMap.get(totalString);
                    ValidationMessage<Origin> message = new ValidationMessage<Origin>(
                            Severity.ERROR,
                            "DuplicatedValuesCheck",
                            entryNumber,
                            duplicateEntryNumber);

                    ValidationResult validationResult = new ValidationResult().append(message);
                    resultsSet.getValidationPlanResult().append(validationResult);
                } else {
                    LOGGER.info("result not found in check uniqueness " + entryNumber);
                }
            } else {
                stringsMap.put(totalString, entryNumber);
            }

            if (count > results.size()) {
                break;
            }
        }
    }

}
