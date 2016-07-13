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
package uk.ac.ebi.embl.flatfile.writer.xml;

import java.io.IOException;
import java.io.StringWriter;

import uk.ac.ebi.embl.api.entry.reference.Book;
import uk.ac.ebi.embl.api.entry.reference.ReferenceFactory;
import uk.ac.ebi.embl.flatfile.FlatFileUtils;
import uk.ac.ebi.ena.xml.SimpleXmlWriter;

public class XmlBookWriterTest extends XmlWriterTest {

	public void testWrite() throws IOException {
		ReferenceFactory referenceFactory = new ReferenceFactory();
		Book book = referenceFactory.createBook(
				null,
				"41ST ANNUAL REGIONAL MEETING OF THE EASTERN NEW YORK, CONNECTICUT VALLEY, NEW YORK CITY, AND NORTHEAST BRANCHES OF THE AMERICAN SOCIETY FOR MICROBIOLOGY",
				"12", "25",
		"American Society of Microbiology Eastern New York Branch, NY, USA");
		book.setYear(FlatFileUtils.getDay("10-SEP-1998"));
		book.addEditor(referenceFactory.createPerson("Unknown", "A."));
		book.addEditor(referenceFactory.createPerson("Unknown", "B."));
		StringWriter writer = new StringWriter();
        assertTrue(new XmlReferenceLocationWriter(entry, book).write(new SimpleXmlWriter(writer)));
        //System.out.print(writer.toString());
        assertEquals(
            "<referenceLocation>\n" +        		
			"(in) Unknown A., Unknown B. (Eds.);\n" +
			"41ST ANNUAL REGIONAL MEETING OF THE EASTERN NEW YORK, CONNECTICUT VALLEY, NEW\n" +
			"YORK CITY, AND NORTHEAST BRANCHES OF THE AMERICAN SOCIETY FOR\n" +
			"MICROBIOLOGY:12-25;\n" +
			"American Society of Microbiology Eastern New York Branch, NY, USA (1998)\n" +
            "</referenceLocation>\n",
    		writer.toString());		
	}
	
	
}
