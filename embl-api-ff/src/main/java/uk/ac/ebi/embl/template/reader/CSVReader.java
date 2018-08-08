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

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private String currentLine;
    private final BufferedReader lineReader;
    private List<String> headerKeys;

    public CSVReader(final InputStream inputReader,
                     final List<TemplateTokenInfo> allTokens,
                     final int expectedMatchNumber) throws TemplateException {

        lineReader = new BufferedReader(new InputStreamReader(inputReader));
        readHeader(expectedMatchNumber, allTokens);
    }

    public CSVLine readTemplateSpreadsheetLine() throws TemplateException {

        CSVLine result = null;

        if (currentLine != null) {

            if (currentLine.isEmpty()) {
                currentLine = readLine();
                return readTemplateSpreadsheetLine();//skip empty lines
            }

            prepareLineForParsing();

            if (currentLine.startsWith(CSVWriter.HEADER_TOKEN)) {
                currentLine = readLine();
                return readTemplateSpreadsheetLine();//as this is the first line which is the header
            }

            if (currentLine.startsWith(FastaSpreadsheetConverter.COMMENT_TOKEN)) {
                currentLine = readLine();
                return readTemplateSpreadsheetLine();
            }

            final TemplateVariables entryTokensMap = new TemplateVariables();
            final String[] currentTokenLine = StringUtils.splitPreserveAllTokens(currentLine, CSVWriter.UPLOAD_DELIMITER);
            if ((currentTokenLine.length) != headerKeys.size()) {
                String lineSummary = currentLine;
                if (currentLine.length() > 10) {
                    lineSummary = currentLine.substring(0, 10);
                }
                throw new TemplateException(
                        "There are " + headerKeys.size() + " fields specified in the header but " + currentTokenLine.length + " values for entry on line " + lineSummary + "..., please check the import file. You may have included comma characters in your field values, please replace these with the ; character.");
            }

            Integer entryNumber = null;
            try {
                entryNumber = new Integer(currentTokenLine[0]);
            } catch (final NumberFormatException e) {
                throw new TemplateException("Expected entry number '" + currentTokenLine[0] + "' can not be converted to a number");
            }

            for (int i = 1; i < currentTokenLine.length; i++) {
                String tokenValue = currentTokenLine[i];
                checkTokenForBannedCharacters(tokenValue);
                tokenValue = tokenValue.replaceAll("<br>", "\n");
                tokenValue = tokenValue.replaceAll(";", ",");
                if (tokenValue.startsWith("\"") && tokenValue.endsWith("\"")) {
                    tokenValue = StringUtils.stripStart(tokenValue, "\"");
                    tokenValue = StringUtils.stripEnd(tokenValue, "\"");
                }
                entryTokensMap.addToken(headerKeys.get(i), tokenValue);
            }

            result = new CSVLine(entryNumber, entryTokensMap);
        }
        currentLine = readLine();
        return result;
    }

    private void checkTokenForBannedCharacters(final String tokenValue) throws TemplateException {
        if (!StringUtils.isBlank(tokenValue)) {
            if (tokenValue.contains(TemplateProcessorConstants.DELIMITER1) ||
                    tokenValue.contains(TemplateProcessorConstants.DELIMITER2) ||
                    tokenValue.contains(TemplateProcessorConstants.DELIMITER3) ||
                    tokenValue.contains(TemplateProcessorConstants.DELIMITER4)) {

                final String message = "Contains illegal characters <d1>, <d2>, <d3> or <d4>";
                throw new TemplateException(message);
            }
        }
    }

    private void prepareLineForParsing() {

        currentLine = currentLine.trim();

        if (currentLine.startsWith("\"")) {
            currentLine = currentLine.replaceFirst("\"", "");//get rid of starting " if present - open office puts these in for strings and they need to be removed
        }

        if(currentLine.endsWith(";")){
            currentLine = StringUtils.removeEnd(currentLine, ";");
        }
    }

    private void readHeader(final int expectedMatchNumber,
                            final List<TemplateTokenInfo> allTokens) throws TemplateException {

        currentLine = readLine();
        if (currentLine == null) {
            throw new TemplateException("Template file is empty");
        }

        String header = null;
        boolean headerFound = false;
        while (currentLine != null) {
            currentLine = currentLine.replaceFirst("\"", "");//get rid of starting " if present - open office puts these in for strings and they need to be removed
            if (currentLine.startsWith(CSVWriter.HEADER_TOKEN)) {
                header = currentLine;
                headerFound = true;
                break;
            }
            currentLine = readLine();
        }

        if (!headerFound) {
            throw new TemplateException("Template header line not found, starts with : " + CSVWriter.HEADER_TOKEN);
        }

        header = header.replaceAll("\"", "");//remove all speech marks - open office puts these in
        final String[] headerTokens = header.split(CSVWriter.UPLOAD_DELIMITER);
        final List<String> recognizedKeys = new ArrayList<String>();
        headerKeys = new ArrayList<String>();

        /**
         * try to match the incoming header names with the token display names of the template. If not recognized, still
         * accept them with the value given as we accept additional fields.
         */
        for (final String headerName : headerTokens) {
            boolean tokenRecognized = false;
            for (final TemplateTokenInfo token : allTokens) {
                if (token.getDisplayName().equals(headerName)) {//does it match a template token name?
                    recognizedKeys.add(token.getName());
                    headerKeys.add(token.getName());
                    tokenRecognized = true;
                    break;
                }
            }
            if (!tokenRecognized) {//add as it comes
                headerKeys.add(headerName);
            }
        }

        final int recognizedTokenNumber = recognizedKeys.size();

        if ((expectedMatchNumber != 0) && (recognizedTokenNumber != expectedMatchNumber)) {
            throw new TemplateException(
                    "Not all variables have been recognized from the column headers. Have you removed fields from the variables since " +
                            "creating the spreadsheet? Check for spelling errors in your column names - names must match " +
                            "the token names you have selected. Check there are no additional characters at the ends of the header line such as ; or ," +
                            "Download a new sample spreadsheet to see what we are expecting you to load." +
                            "Additional columns not corresponding to variables are permitted.");
        }
    }

    private String readLine() throws TemplateException {
        try {
            return lineReader.readLine();
        } catch (final IOException e) {
            throw new TemplateException(e);
        }
    }
}
