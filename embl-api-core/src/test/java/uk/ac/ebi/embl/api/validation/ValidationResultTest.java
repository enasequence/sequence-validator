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

package uk.ac.ebi.embl.api.validation;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class ValidationResultTest {

    private final static String TEST_MESSAGE = "TEST_MESSAGE";
    private final static String ORIGIN_MESSAGE_1 = "ORIGIN_MESSAGE_1";
    private final static String ORIGIN_MESSAGE_2 = "ORIGIN_MESSAGE_2";
    private final static String CURATOR_MESSAGE = "CURATOR_MESSAGE";
    private final static String REPORT_MESSAGE = "REPORT_MESSAGE";
    private final static String TARGET_ORIGIN = "TARGET_ORIGIN";

    @Test
    public void testWriteTextMessageWithoutOrigin() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str, TARGET_ORIGIN);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [TARGET_ORIGIN]\n", str.toString());
    }

    @Test
    public void testWriteTextMessageWithOneOrigin() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage);
        validationMessage.addOrigin(new DefaultOrigin(ORIGIN_MESSAGE_1));
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str, TARGET_ORIGIN);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [TARGET_ORIGIN, ORIGIN_MESSAGE_1]\n", str.toString());
    }

    @Test
    public void testWriteTextMessageWithTwoOrigins() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        validationMessage.addOrigin(new DefaultOrigin(ORIGIN_MESSAGE_1));
        validationMessage.addOrigin(new DefaultOrigin(ORIGIN_MESSAGE_2));
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1, ORIGIN_MESSAGE_2]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1, ORIGIN_MESSAGE_2]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [ORIGIN_MESSAGE_1, ORIGIN_MESSAGE_2]\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setMessageFormatter(ValidationMessage.TEXT_MESSAGE_FORMATTER_TRAILING_LINE_END);
        validationResult.writeMessages(str, TARGET_ORIGIN);
        assertEquals("ERROR: Missing message: TEST_MESSAGE [TARGET_ORIGIN, ORIGIN_MESSAGE_1, ORIGIN_MESSAGE_2]\n", str.toString());
    }

    @Test
    public void testWriteTextMessageWithCuratorMessage() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        validationMessage.setCuratorMessage(CURATOR_MESSAGE);
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage);
        validationResult.writeMessages(str);
        assertEquals(
                "ERROR: Missing message: TEST_MESSAGE\n\n" +
                "********\n" +
                "Curator message: CURATOR_MESSAGE\n" +
                "********", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setWriteCuratorMessage(false);
        validationResult.writeMessages(str);
        assertEquals(
                "ERROR: Missing message: TEST_MESSAGE\n", str.toString());
    }


    @Test
    public void testWriteTextMessageWithReportMessage() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        validationMessage.setReportMessage(REPORT_MESSAGE);
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage);
        validationResult.writeMessages(str);
        assertEquals("ERROR: Missing message: TEST_MESSAGE\n", str.toString());

        str.getBuffer().setLength(0);
        validationResult.setWriteReportMessage(true);
        validationResult.writeMessages(str);
        assertEquals(
                "ERROR: Missing message: TEST_MESSAGE\n\n" +
                "********\n" +
                "Report message:\n" +
                "\n" +
                "REPORT_MESSAGE\n" +
                "********", str.toString());
    }
    
    @Test
    public void testWriteMessagestats() throws IOException {
        StringWriter str = new StringWriter();
        ValidationMessage validationMessage1= new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        ValidationMessage validationMessage2 = new ValidationMessage(Severity.ERROR, TEST_MESSAGE);
        ValidationResult validationResult = new ValidationResult();
        validationResult.append(validationMessage1);
        validationResult.append(validationMessage2);
        validationResult.writeMessageStats(str);
        assertEquals("2	TEST_MESSAGE	Missing message: TEST_MESSAGE\n", str.toString());
        }
}
