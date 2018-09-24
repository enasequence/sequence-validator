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

package uk.ac.ebi.embl.api.validation.report;

import uk.ac.ebi.embl.api.validation.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

public class DefaultSubmissionReporter implements SubmissionReporter {

    private final HashSet<Severity> reportSeverity;

    public DefaultSubmissionReporter(HashSet<Severity> reportSeverity) {
        this.reportSeverity = reportSeverity;
    }

    // Create message
    //

    public static ValidationMessage
    createValidationMessage(Severity severity, String error) {
        return createValidationMessage(severity, error, null);
    }

    public static ValidationMessage
    createValidationMessage(Severity severity, String error, Origin origin) {
        ValidationMessage validationMessage = new ValidationMessage<>(severity, ValidationMessage.NO_KEY);
        validationMessage.setMessage(error);
        validationMessage.append(origin);
        return validationMessage;
    }

    // Write to path.

    @Override
    public void
    writeToFile(Path reportFile, ValidationPlanResult validationPlanResult, String targetOrigin) {
        writeMessages(reportFile, (s) -> {
            for( ValidationMessage validationMessage: validationPlanResult.getMessages() ) {
                writeMessage(s, validationMessage, targetOrigin);
            }});
    }

    @Override
    public void
    writeToFile(Path reportFile, ValidationPlanResult validationPlanResult) {
        writeMessages(reportFile, (s) -> {
            for( ValidationMessage validationMessage: validationPlanResult.getMessages() ) {
                writeMessage(s, validationMessage, null /* targetOrigin */);
            }});
    }

    @Override
    public void
    writeToFile(Path reportFile, ValidationResult validationResult, String targetOrigin )
    {
        writeMessages(reportFile, (s) -> {
            for( ValidationMessage validationMessage: validationResult.getMessages() ) {
                writeMessage(s, validationMessage, targetOrigin);
            }});
    }

    @Override
    public void
    writeToFile(Path reportFile, ValidationResult validationResult )
    {
        writeMessages(reportFile, (s) -> {
            for( ValidationMessage validationMessage: validationResult.getMessages() ) {
                writeMessage(s, validationMessage, null /* targetOrigin */);
            }});
    }

    @Override
    public void
    writeToFile(Path reportFile, ValidationMessage validationMessage )
    {
        writeMessages(reportFile, (s) -> writeMessage(s, validationMessage));
    }

    @Override
    public void
    writeToFile(Path reportFile, Severity severity, String message, Origin origin )
    {
        writeMessages(reportFile, (s) -> writeMessage(s, createValidationMessage(severity, message, origin)));
    }

    @Override
    public void
    writeToFile(Path reportFile, Severity severity, String message )
    {
        writeMessages(reportFile, (s) -> writeMessage(s, createValidationMessage(severity, message, null /* origin */)));
    }


    // Write messages
    //

    private interface WriteCallback {
        void write(OutputStream strm) throws IOException;
    }

    private void
    writeMessages(Path reportFile, WriteCallback callback) {
        OutputStream strm = System.out;
        if (reportFile != null) {
            try {
                strm = Files.newOutputStream(reportFile,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
            } catch (IOException e) {
                //
            }
        }
        writeMessages(strm, callback);
    }

    private void
    writeMessages(OutputStream strm, WriteCallback callback) {
        try {
            callback.write(strm);
        }
        catch( IOException e ) {
            //
        }
        finally {
            if (strm != System.out &&
                    strm != System.err) {
                try {
                    strm.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }

    // Write message
    //

    private void
    writeMessage(OutputStream strm, ValidationMessage validationMessage ) throws IOException {
        writeMessage(strm, validationMessage, null);
    }

    private void
    writeMessage(OutputStream strm, ValidationMessage validationMessage, String targetOrigin ) throws IOException {
        if (reportSeverity.contains(validationMessage.getSeverity())) {
            strm.write(formatMessage(validationMessage, targetOrigin).getBytes(StandardCharsets.UTF_8));
        }
    }

    // Format message
    //

    private static String
    formatMessage(ValidationMessage validationMessage, String targetOrigin)
    {
        try
        {
            StringWriter str = new StringWriter();
            validationMessage.writeMessage( str, targetOrigin ); // ValidationMessage::messageFormatter
            return str.toString();
        } catch ( IOException e )
        {
            return e.toString();
        }
    }
}