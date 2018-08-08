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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemplateVariables {
    private static final long serialVersionUID = 1L;
    private Map<String, String> variables;

    public TemplateVariables() {
        variables = new HashMap<String, String>();
    }

    public TemplateVariables(Map<String, String> entryTokenMap) {
        this.variables = entryTokenMap;
    }

    public boolean containsToken(String name) {
        return variables.containsKey(name);
    }

    public String getTokenValue(String tokenName) {
        return variables.get(tokenName);
    }

    public void addToken(String tokenName, String value) {
        variables.put(tokenName, value);
    }

    public Set<String> getTokenNames() {
        return variables.keySet();
    }

    public boolean isEmpty() {
        return variables.isEmpty();
    }

    public void removeToken(String name) {
        variables.remove(name);
    }
}
