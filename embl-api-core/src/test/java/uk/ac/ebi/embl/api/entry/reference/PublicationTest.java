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
package uk.ac.ebi.embl.api.entry.reference;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.EntryFactory;
import uk.ac.ebi.embl.api.entry.XRef;

public class PublicationTest {

	private Publication publication;
	
	@Before
	public void setUp() throws Exception {
		publication = new Publication();
	}

	@Test
	public void testPublication() {
		assertNull(publication.getTitle());
		assertNull(publication.getConsortium());
		assertTrue(publication.getAuthors().isEmpty());
		assertTrue(publication.getXRefs().isEmpty());
		publication.setTitle("title");
		publication.setConsortium("consortium");
		assertEquals("title", publication.getTitle());
		assertEquals("consortium", publication.getConsortium());
		assertTrue(publication.getAuthors().isEmpty());
		assertTrue(publication.getXRefs().isEmpty());
	}

	@Test
	public void testGetAuthors() {
		assertEquals(0, publication.getAuthors().size());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetAuthors_UnmodifiableList() {
		publication.getAuthors().add(null);
	}	

	@Test
	public void testAddAuthor() {
		Person person = new Person("X");
		assertTrue(publication.addAuthor(person));
		assertEquals(person, publication.getAuthors().get(0));
	}

	@Test
	public void testAddAuthors() {
		Person person = new Person("X");
		assertTrue(publication.addAuthors(Arrays.asList(person)));
		assertEquals(person, publication.getAuthors().get(0));
	}

	@Test
	public void testAddAuthors_Null() {
		assertFalse(publication.addAuthors(null));
		assertTrue(publication.getAuthors().isEmpty());
	}
	
	@Test
	public void testRemoveAuthor() {
		Person personX = new Person("X");
		Person personY = new Person("Y");
		assertFalse(publication.removeAuthor(personX));
		
		publication.addAuthor(personX);
		assertEquals(1, publication.getAuthors().size());
		
		assertFalse(publication.removeAuthor(personY));
		assertEquals(1, publication.getAuthors().size());
		
		assertTrue(publication.removeAuthor(personX));
		assertEquals(0, publication.getAuthors().size());
	}

	@Test
	public void testGetXRefs() {
		assertEquals(0, publication.getXRefs().size());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetXRefs_UnmodifiableList() {
		publication.getXRefs().add(null);
	}	

	@Test
	public void testAddXRef() {
		XRef xRef = new EntryFactory().createXRef("db", "pa", "sa");
		assertTrue(publication.addXRef(xRef));
		assertEquals(xRef, publication.getXRefs().get(0));
	}

	@Test
	public void testAddXRefs() {
		XRef xRef = new EntryFactory().createXRef("db", "pa", "sa");
		assertTrue(publication.addXRefs(Arrays.asList(xRef)));
		assertEquals(xRef, publication.getXRefs().get(0));
	}

	@Test
	public void testAddXRefs_Null() {
		assertFalse(publication.addXRefs(null));
		assertTrue(publication.getXRefs().isEmpty());
	}
	
	@Test
	public void testRemoveXRef() {
		EntryFactory entryFactory = new EntryFactory();
		XRef xRef = entryFactory.createXRef("db", "pa", "sa");
		XRef x = entryFactory.createXRef("x", "x", "x");
		assertFalse(publication.removeXRef(xRef));
		
		publication.addXRef(xRef);
		assertEquals(1, publication.getXRefs().size());
		
		assertFalse(publication.removeXRef(x));
		assertEquals(1, publication.getXRefs().size());
		
		assertTrue(publication.removeXRef(xRef));
		assertEquals(0, publication.getXRefs().size());
	}
	
	@Test
	public void testHashCode() {
		publication.hashCode();
	}

	@Test
	public void testEquals() {
		assertTrue(publication.equals(publication));
		assertTrue(publication.equals(new Publication()));
		Publication publication2 = new Publication();		
		publication.setTitle("title");
		assertFalse(publication.equals(publication2));
		publication2.setTitle("title");
		assertTrue(publication.equals(publication2));
		publication.setConsortium("consortium");
		assertFalse(publication.equals(publication2));
		publication2.setConsortium("consortium");
		assertTrue(publication.equals(publication2));
		publication.addAuthor(
				(new ReferenceFactory()).createPerson("surname", "firstname"));
		assertFalse(publication.equals(publication2));
		publication2.addAuthor(
				(new ReferenceFactory()).createPerson("surname", "firstname"));
		assertTrue(publication.equals(publication2));
		publication.addXRef(
				(new EntryFactory()).createXRef("database", "accession"));
		assertFalse(publication.equals(publication2));
		publication2.addXRef(
				(new EntryFactory()).createXRef("database", "accession"));
		assertTrue(publication.equals(publication2));
	}

	@Test
	public void testEquals_WrongObject() {
		assertFalse(publication.equals(new String()));
	}
	
	@Test
	public void testToString() {
		assertNotNull(publication.toString());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(publication.compareTo(publication) ==0);
		assertTrue(publication.compareTo(new Publication()) == 0);
		Publication publication2 = new Publication();
		// null < not null
		publication.setTitle("title");
		assertTrue(publication.compareTo(publication2) > 0);
		publication2.setTitle("title");
		assertTrue(publication.compareTo(publication2) == 0);
		publication.setConsortium("consortium");
		assertTrue(publication.compareTo(publication2) > 0);
		publication2.setConsortium("consortium");
		assertTrue(publication.compareTo(publication2) == 0);
		publication.addAuthor(
				(new ReferenceFactory()).createPerson("surname", "firstname"));
		assertTrue(publication.compareTo(publication2) > 0);
		publication2.addAuthor(
				(new ReferenceFactory()).createPerson("surname", "firstname"));
		assertTrue(publication.compareTo(publication2) == 0);
		publication.addXRef(
				(new EntryFactory()).createXRef("database", "accession"));
		assertTrue(publication.compareTo(publication2) > 0);
		publication2.addXRef(
				(new EntryFactory()).createXRef("database", "accession"));
		assertTrue(publication.compareTo(publication2) == 0);
	}	
}
