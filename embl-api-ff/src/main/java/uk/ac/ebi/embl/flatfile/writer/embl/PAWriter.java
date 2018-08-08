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

/**
 * 
 */
package uk.ac.ebi.embl.flatfile.writer.embl;

import java.io.IOException;
import java.io.Writer;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.flatfile.EmblPadding;
import uk.ac.ebi.embl.flatfile.writer.FlatFileWriter;
import uk.ac.ebi.embl.flatfile.writer.WrapType;

/**
 * Flat file writer for PA line for the CDS product.
 * 
 * @author simonk
 * 
 */
public class PAWriter extends FlatFileWriter {

	private String accession;

	public PAWriter(Entry entry, WrapType wrapType) {
		super(entry, wrapType);

		this.accession = entry.getPrimaryAccession() + "."
				+ entry.getSequence().getVersion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.embl.flatfile.writer.FlatFileWriter#write(java.io.Writer)
	 */
	@Override
	public boolean write(Writer writer) throws IOException {

		writeBlock(writer, EmblPadding.PA_PADDING, accession);

		return true;
	}

}
