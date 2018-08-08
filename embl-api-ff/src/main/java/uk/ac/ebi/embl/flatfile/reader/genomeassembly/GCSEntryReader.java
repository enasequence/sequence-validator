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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationMessageManager;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.flatfile.reader.FlatFileReader;
import uk.ac.ebi.embl.flatfile.validation.FlatFileValidations;

public abstract class GCSEntryReader implements FlatFileReader<Object> 
{
	ValidationResult validationResult =new ValidationResult();
	File file=null;

	public GCSEntryReader() {
        ValidationMessageManager.addBundle(FlatFileValidations.GENOMEASSEMBLY_FLAT_FILE_BUNDLE);

	}
	 protected void error(int lineNumber,String messageKey, Object... params) {
			validationResult.append(FlatFileValidations.message(lineNumber, Severity.ERROR, messageKey, params));
				
	    }

	    protected void warning(int lineNumber,String messageKey, Object... params) {
	    	validationResult.append(FlatFileValidations.message(lineNumber, Severity.WARNING, messageKey, params));
	    }
	
	public static BufferedReader getBufferedReader (File file) throws FileNotFoundException, IOException
	{
		
		if (file.getName().matches("^.+\\.gz$")||file.getName().matches("^.+\\.gzip$"))
		{
			GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
			return new BufferedReader(new InputStreamReader(gzip));
		}
		else if(file.getName().matches("^.+\\.bz2$")||file.getName().matches("^.+\\.bzip2$"))
		{
			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(new FileInputStream(file));
			return new BufferedReader(new InputStreamReader(bzIn));
		}
		else
		{
			return new BufferedReader(new FileReader(file));
		}
		
	}
}
