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
package uk.ac.ebi.embl.api.translation;

import java.util.HashMap;
import java.util.Map;

public class TranslationTableFactory {

	public TranslationTable createTranslationTable(Integer number) {
		if (number == null) {
			return null;
		}
		return TRANSLATION_TABLES.get(number);
	}

	private static final Map<Integer, TranslationTable> TRANSLATION_TABLES = 
		new HashMap<Integer, TranslationTable>();

	static {
		addTranslationTable(TranslationTableDescriptor.getDescriptor(1));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(2));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(3));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(4));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(5));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(6));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(9));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(10));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(11));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(12));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(13));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(14));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(16));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(21));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(22));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(23));
		addTranslationTable(TranslationTableDescriptor.getDescriptor(24));
	}

	private static void addTranslationTable(
			TranslationTableDescriptor descriptor) {
		Map<String, Character> startCodonMap = new HashMap<String, Character>();
		Map<String, Character> otherCodonMap = new HashMap<String, Character>();
		char[] codon = new char[3];
		int i = 0;
		char[] bases = { 't', 'c', 'a', 'g' };
		for (char base1 : bases) {
			codon[0] = base1;
			for (char base2 : bases) {
				codon[1] = base2;
				for (char base3 : bases) {
					codon[2] = base3;
					char aminoAcid = descriptor.getAminoAcids().charAt(i);
					otherCodonMap.put(new String(codon), aminoAcid);
					if (descriptor.getStarts().charAt(i) == 'M') {
						aminoAcid = 'M';
					}
					startCodonMap.put(new String(codon), aminoAcid);
					++i;
				}
			}
		}
		TRANSLATION_TABLES.put(descriptor.getNumber(), new TranslationTable(
				descriptor.getNumber(), descriptor.getName(), startCodonMap,
				otherCodonMap));
	}
}
