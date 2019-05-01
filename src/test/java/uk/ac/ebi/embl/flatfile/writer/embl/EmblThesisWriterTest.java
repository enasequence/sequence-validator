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
package uk.ac.ebi.embl.flatfile.writer.embl;

import java.io.IOException;
import java.io.StringWriter;

import uk.ac.ebi.embl.api.entry.reference.ReferenceFactory;
import uk.ac.ebi.embl.api.entry.reference.Thesis;
import uk.ac.ebi.embl.flatfile.FlatFileUtils;
import uk.ac.ebi.embl.flatfile.writer.embl.RLWriter;

public class EmblThesisWriterTest extends EmblWriterTest {

    public void testWrite_Thesis() throws IOException {
		ReferenceFactory referenceFactory = new ReferenceFactory();
    	Thesis thesis = referenceFactory.createThesis("My most important work",
    			FlatFileUtils.getDay("10-SEP-1998"), "Universitaet Muenchen");    	
        StringWriter writer = new StringWriter();
        assertTrue(new RLWriter(entry, thesis, wrapType).write(writer));
        // System.out.print(writer.toString());
        assertEquals(
        		"RL   Thesis (1998), Universitaet Muenchen.\n",
        		writer.toString());
    }
    
    public void testWrite_EmptyThesis() throws IOException {
		ReferenceFactory referenceFactory = new ReferenceFactory();
    	Thesis thesis = referenceFactory.createThesis(null,
    			null, null);
        StringWriter writer = new StringWriter();
        assertTrue(new RLWriter(entry, thesis, wrapType).write(writer));
        // System.out.print(writer.toString());
        assertEquals(
        		"RL   Thesis ().\n",
        		writer.toString());
    }
}