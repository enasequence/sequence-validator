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
package uk.ac.ebi.embl.flatfile.reader.genbank;

import java.io.IOException;

import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.flatfile.reader.genbank.OriginReader;

public class OriginReaderTest extends GenbankReaderTest {

	public void testRead() throws IOException {
		initLineReader(
                "ORIGIN     test test test"
		);
		ValidationResult result = (new OriginReader(lineReader)).read(entry);
		assertEquals(0, result.count(Severity.ERROR));
	}

	public void testRead_Empty() throws IOException {
		initLineReader(
                "ORIGIN"
		);
		ValidationResult result = (new OriginReader(lineReader)).read(entry);
		assertEquals(0, result.count(Severity.ERROR));
	}
}
