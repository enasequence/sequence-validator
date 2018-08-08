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

package uk.ac.ebi.embl.api.validation.check.entries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.EntryFactory;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationResult;

public class Entry_NameCheckTest
{
	private Entry_NameCheck check;
	private Entry entry1, entry2, entry3, entry4;
	private EntryFactory entryFactory;
	private ArrayList<Entry> entrySet;

	@Before
	public void setUp()
	{
		check = new Entry_NameCheck();
		entrySet = new ArrayList<Entry>();
		entryFactory = new EntryFactory();
		entry1 = entryFactory.createEntry();
		entry2 = entryFactory.createEntry();
		entry3 = entryFactory.createEntry();
		entry4 = entryFactory.createEntry();
		entry1.setSubmitterAccession("_ctg000001");
		entry2.setSubmitterAccession("_ctg000002");
		entry3.setSubmitterAccession("_ctg000001");
	}

	@Test
	public void testCheck_NoEntrySet()
	{
		assertTrue(check.check(null).isValid());
	}

	@Test
	public void testCheck_NoEntries()
	{
		ValidationResult result = check.check(entrySet);
		assertTrue(result.isValid());
	}

	@Test
	public void testCheck_NoSubmitterAccession()
	{
		entrySet.add(entry4);
		entrySet.add(entry4);
		ValidationResult result = check.check(entrySet);
		assertTrue(result.isValid());
	}

	@Test
	public void testCheck_duplicateSubmitterAccession()
	{
		entrySet.add(entry1);
		entrySet.add(entry2);
		entrySet.add(entry3);
		ValidationResult result = check.check(entrySet);
		assertTrue(!result.isValid());
		assertEquals(1, result.count("Entry_NameCheck1", Severity.ERROR));
	}

	@Test
	public void testCheck_NoduplicateSubmitterAccession()
	{
		entrySet.add(entry1);
		entrySet.add(entry2);
		ValidationResult result = check.check(entrySet);
		assertTrue(result.isValid());

	}

}
