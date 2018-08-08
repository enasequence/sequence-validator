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

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.validation.ValidationPlanResult;

public class TemplateProcessorResultSet {
    private ValidationPlanResult validationPlanResult;
    private String entryString;
    private Integer entryNumber;
    private Entry entry;

    public TemplateProcessorResultSet(ValidationPlanResult validationResult, String entryString, Integer entryNumber) {
        this.validationPlanResult = validationResult;
        this.entryString = entryString;
        this.entryNumber = entryNumber;
    }

    public TemplateProcessorResultSet(String entryString) {
        this.entryString = entryString;
        this.validationPlanResult = new ValidationPlanResult();
    }

    public TemplateProcessorResultSet() {
        this.validationPlanResult = new ValidationPlanResult();
    }

    public ValidationPlanResult getValidationPlanResult() {
        return validationPlanResult;
    }

    public void setValidationPlanResult(ValidationPlanResult validationPlanResult) {
        this.validationPlanResult = validationPlanResult;
    }

    public void setEntryString(String entryString) {
        this.entryString = entryString;
    }

    public String getEntryString() {
        return entryString;
    }

    public void setEntryNumber(Integer entryNumber) {
        this.entryNumber = entryNumber;
    }

    public Integer getEntryNumber() {
        return entryNumber;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "TemplateProcessorResultSet{" +
                "validationPlanResult=" + validationPlanResult +
                ", entryString='" + entryString + '\'' +
                ", entryNumber=" + entryNumber +
                '}';
    }
}
