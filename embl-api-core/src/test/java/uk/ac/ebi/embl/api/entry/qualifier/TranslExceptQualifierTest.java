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
package uk.ac.ebi.embl.api.entry.qualifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.location.LocalBase;
import uk.ac.ebi.embl.api.entry.location.LocalRange;
import uk.ac.ebi.embl.api.entry.qualifier.TranslExceptQualifier;
import uk.ac.ebi.embl.api.validation.ValidationException;

public class TranslExceptQualifierTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testValidQualifier1() throws ValidationException {
		TranslExceptQualifier qual = new TranslExceptQualifier("(pos:213..215,aa:Phe)");
		assertEquals(qual.getAminoAcid().getAbbreviation(), "Phe");
		assertEquals(qual.getAminoAcid().getLetter(), new Character('F'));
		assertEquals(qual.getLocation().getBeginPosition(), new Long(213));
		assertEquals(qual.getLocation().getEndPosition(), new Long(215));
		assertTrue(qual.getLocation() instanceof LocalRange);
	}

	@Test
	public void testValidQualifier2() throws ValidationException {
		TranslExceptQualifier qual = new TranslExceptQualifier("(pos:213,aa:TERM)");
		assertEquals(qual.getAminoAcid().getAbbreviation(), "TERM");
		assertEquals(qual.getAminoAcid().getLetter(), new Character('*'));
		assertEquals(qual.getLocation().getBeginPosition(), new Long(213));
		assertEquals(qual.getLocation().getEndPosition(), new Long(213));
		assertTrue(qual.getLocation() instanceof LocalBase);
	}
	
	@Test(expected=ValidationException.class)
	public void testInvalidQualifier1() throws ValidationException {
		TranslExceptQualifier qual = new TranslExceptQualifier("os:213..215,aa:Phe)");
		assertEquals(qual.getLocation().getBeginPosition(), new Long(213));
	}

	@Test(expected=ValidationException.class)
	public void testInvalidQualifier2() throws ValidationException {
		TranslExceptQualifier qual = new TranslExceptQualifier("os:213..215,aa:Phe)");
		assertEquals(qual.getAminoAcid().getAbbreviation(), "Phe");
	}
}
