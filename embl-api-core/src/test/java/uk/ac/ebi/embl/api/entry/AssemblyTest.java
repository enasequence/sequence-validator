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
package uk.ac.ebi.embl.api.entry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.location.LocalRange;
import uk.ac.ebi.embl.api.entry.location.LocationFactory;
import uk.ac.ebi.embl.api.entry.location.RemoteRange;

public class AssemblyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAssembly() {
		LocationFactory locationFactory = new LocationFactory();
		RemoteRange primary = locationFactory.createRemoteRange(
				"x", 1, 1L, 3L, true);
		LocalRange secondary = locationFactory.createLocalRange(2L, 4L);		
		
		Assembly assembly = new Assembly(primary, secondary);
		assertSame(primary, assembly.getPrimarySpan());
		assertSame(secondary, assembly.getSecondarySpan());
	}

	@Test
	public void testAssembly_Nulls() {
		Assembly assembly = new Assembly(null, null);
		assertNull(assembly.getPrimarySpan());
		assertNull(assembly.getSecondarySpan());
	}
	
	@Test
	public void testHashCode() {
		new Assembly(null, null).hashCode();

		LocationFactory locationFactory = new LocationFactory();
		RemoteRange primary = locationFactory.createRemoteRange(
				"x", 1, 1L, 3L, true);
		LocalRange secondary = locationFactory.createLocalRange(2L, 4L);		
		new Assembly(primary, secondary).hashCode();
	}
	
	@Test
	public void testEquals() {
		LocationFactory factory = new LocationFactory();
		Assembly assembly1 = new Assembly(
				factory.createRemoteRange("x", 1, 1L, 3L), 
				factory.createLocalRange(2L, 4L));
		Assembly assembly2 = new Assembly(
				factory.createRemoteRange("x", 1, 1L, 3L), 
				factory.createLocalRange(2L, 4L));
		
		assertTrue(assembly1.equals(assembly1));
		assertTrue(assembly1.equals(assembly2));
		assertTrue(assembly2.equals(assembly1));
		
		Assembly assembly3 = new Assembly(
				factory.createRemoteRange("y", 1, 1L, 3L), 
				factory.createLocalRange(2L, 4L));
		assertFalse(assembly1.equals(assembly3));

		Assembly assembly4 = new Assembly(
				factory.createRemoteRange("x", 1, 1L, 3L), 
				factory.createLocalRange(3L, 4L));
		assertFalse(assembly1.equals(assembly4));
	}
	
	@Test
	public void testEquals_WrongObject() {
		assertFalse(new Assembly(null, null).equals(new String()));
	}

	@Test
	public void testToString() {
		assertNotNull(new Assembly(null, null).toString());

		LocationFactory locationFactory = new LocationFactory();
		RemoteRange primary = locationFactory.createRemoteRange(
				"x", 1, 1L, 3L, true);
		LocalRange secondary = locationFactory.createLocalRange(2L, 4L);		
		assertNotNull(new Assembly(primary, secondary).toString());
	}

}
