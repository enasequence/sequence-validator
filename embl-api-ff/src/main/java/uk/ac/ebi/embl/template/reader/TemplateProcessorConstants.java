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

public class TemplateProcessorConstants {
    /**
     * reserved tokens
     */
    public static final String CITATION_TOKEN = "CITATIONS";
    public static final String SEQUENCE_TOKEN = "SEQUENCE";
    public static final String COMMENTS_TOKEN = "COMMENTS";
    public static final String SEQUENCE_LENGTH_TOKEN = "SEQUENCE_LENGTH";
    public static final String STATUS_DATE_TOKEN = "STATUS";
    public static final String FEATURES_UPLOAD_TOKEN = "FEATURES_UPLOAD";
    public static final String TOPOLOGY_TOKEN = "TOPOLOGY";//used in feature table upload template
    public static final String PROJECT_ID_TOKEN = "PROJECT_ID";//used in feature table upload template
    public static final String DESCRIPTION_TOKEN = "DESCRIPTION";//used in feature table upload template

    /**
     * used for writing and reading the DETAILS.txt properties file when processing templates with the forms and command
     * line tool for processing templates from forms output
     */
    public static final String TEMPLATE_ID_TOKEN = "TEMPLATE_ID";
    public static final String TEMPLATE_VERSION_TOKEN = "TEMPLATE_VERSION";
    public static final String HOLD_DATE_TOKEN = "HOLD_DATE";

    public static final String TOKEN_DELIMITER = "{";
    public static final String TOKEN_CLOSE_DELIMITER = "}";
    public static final String SECTION_DELIMITER = "{{";
    public static final String SECTION_CLOSE_DELIMITER = "}}";

    /**
     * delimiters for string storage of constant and variable hashmaps
     */
    public static final String DELIMITER1 = "<d1>";
    public static final String DELIMITER2 = "<d2>";
    public static final String DELIMITER3 = "<d3>";
    public static final String DELIMITER4 = "<d4>";

    /**
     * the bundle name for validation messages produced by template processing
     */
    public static final String TEMPLATE_MESSAGES_BUNDLE = "ValidationTemplateMessages";

    /**
     * VERY LARGE TEMPLATE SUBMISSION THRESHOLD - MEANS WILL BE PROCESSED DIFFERENTLY
     */
    public static final int TEMPLATE_MEGA_ENTRY_SIZE = 1000;
}
