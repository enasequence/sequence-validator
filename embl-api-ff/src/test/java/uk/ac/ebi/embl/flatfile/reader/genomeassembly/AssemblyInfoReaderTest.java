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

package uk.ac.ebi.embl.flatfile.reader.genomeassembly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import uk.ac.ebi.embl.api.entry.genomeassembly.AssemblyInfoEntry;
import uk.ac.ebi.embl.api.validation.ValidationResult;

public class AssemblyInfoReaderTest 
{
	@Test
	public void testRead_validFile() throws IOException 
	{
		String fileName=null;
		URL url = AssemblyInfoReaderTest.class.getClassLoader().getResource( "valid_assembly_info.txt");
		if (url != null)
			fileName = url.getPath().replaceAll("%20", " ");
		AssemblyInfoReader reader = new AssemblyInfoReader(new File(fileName));
		ValidationResult parseResult=reader.read();
		assertTrue(parseResult.isValid());
		AssemblyInfoEntry entry=(AssemblyInfoEntry) reader.getEntry();
		assertEquals("fAstCal1.2",entry.getName());
		assertEquals("45",entry.getCoverage());
		assertEquals("assembly",entry.getProgram());
		assertEquals("assembly",entry.getPlatform());
		assertEquals(new Integer(0),entry.getMinGapLength());
		assertEquals("genomic DNA",entry.getMoleculeType());
		assertEquals("SAMEA104026430",entry.getSampleId());
		assertEquals("PRJEB24325",entry.getStudyId());
	}

	@Test
	public void testRead_EmptyFile() throws IOException 
	{
		String fileName=null;
		URL url = AssemblyInfoReaderTest.class.getClassLoader().getResource( "empty_assembly_info.txt");
		if (url != null)
			fileName = url.getPath().replaceAll("%20", " ");
		AssemblyInfoReader reader = new AssemblyInfoReader(new File(fileName));
		ValidationResult parseResult=reader.read();
		assertTrue(!parseResult.isValid());
		assertEquals(1,parseResult.getMessages("EmptyFileCheck").size());
	}

	@Test
	public void testRead_invalidFileFormat() throws IOException 
	{
		String fileName=null;
		URL url = AssemblyInfoReaderTest.class.getClassLoader().getResource( "invalid_assembly_info.txt");
		if (url != null)
			fileName = url.getPath().replaceAll("%20", " ");
		AssemblyInfoReader reader = new AssemblyInfoReader(new File(fileName));
		ValidationResult parseResult=reader.read();
		assertTrue(!parseResult.isValid());
		assertEquals(1,parseResult.getMessages("invalidfieldValue").size());
	}

	@Test
	public void testRead_withnullValues() throws IOException 
	{
		String fileName=null;
		URL url = AssemblyInfoReaderTest.class.getClassLoader().getResource( "empty_value_assembly_info.txt");
		if (url != null)
			fileName = url.getPath().replaceAll("%20", " ");
		AssemblyInfoReader reader = new AssemblyInfoReader(new File(fileName));
		reader.read();
		AssemblyInfoEntry entry=(AssemblyInfoEntry) reader.getEntry();
		assertEquals("fAstCal1.2",entry.getName());
		assertEquals("45",entry.getCoverage());
		assertEquals("assembly",entry.getProgram());
		assertEquals(null,entry.getPlatform());
		assertEquals(new Integer(0),entry.getMinGapLength());
		assertEquals("genomic DNA",entry.getMoleculeType());
		assertEquals(null,entry.getSampleId());
		assertEquals("PRJEB24325",entry.getStudyId());	}

}
