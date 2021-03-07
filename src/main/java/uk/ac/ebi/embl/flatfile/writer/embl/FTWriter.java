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
import java.io.Writer;
import java.util.Collections;
import java.util.Vector;

import uk.ac.ebi.embl.flatfile.EmblPadding;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.flatfile.writer.FeatureWriter;
import uk.ac.ebi.embl.flatfile.writer.FlatFileWriter;
import uk.ac.ebi.embl.flatfile.writer.WrapType;

/** Flat file writer for the feature table.
 */
public class FTWriter extends FlatFileWriter {

	public FTWriter(Entry entry, boolean sortFeatures,
					boolean sortQualifiers, WrapType wrapType) {
		super(entry, wrapType);
		this.sortFeatures = sortFeatures;
		this.sortQualifiers = sortQualifiers;
	}

	public FTWriter(Entry entry, boolean sortFeatures,
					boolean sortQualifiers, boolean excludeSource, WrapType wrapType) {
		super(entry, wrapType);
		this.sortFeatures = sortFeatures;
		this.sortQualifiers = sortQualifiers;
		this.excludeSource = excludeSource;
	}

	private boolean sortFeatures;
	private boolean sortQualifiers;
	private boolean excludeSource;

	public boolean write(Writer writer) throws IOException {
		Vector<Feature> features = new Vector<Feature>(entry.getFeatures());
		if (features == null ||
			features.size() == 0) {
			return false;
		}
		if (excludeSource &&
				features.size() == 1 &&
				isSourceFeature(features.get(0))) {
			return false;
		}
		if (sortFeatures) {
			Collections.sort(features);
		}
		new FHWriter(entry).write(writer);
		for (Feature feature : features) {
			if (excludeSource && isSourceFeature(feature))
				continue;
            writeFeature(writer, feature);
        }
		return true;		
	}

	private boolean isSourceFeature(Feature feature) {
		return feature.getName().equals(Feature.SOURCE_FEATURE_NAME);
	}

    protected void writeFeature(Writer writer, Feature feature) throws IOException {
        new FeatureWriter(entry, feature, sortQualifiers, wrapType,
        		EmblPadding.FEATURE_PADDING,
        		EmblPadding.QUALIFIER_PADDING).write(writer);
    }
}
