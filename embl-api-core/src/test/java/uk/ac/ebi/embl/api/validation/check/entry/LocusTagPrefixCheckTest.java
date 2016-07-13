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
package uk.ac.ebi.embl.api.validation.check.entry;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.EntryFactory;
import uk.ac.ebi.embl.api.entry.Text;
import uk.ac.ebi.embl.api.entry.XRef;
import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.api.entry.feature.FeatureFactory;
import uk.ac.ebi.embl.api.entry.location.Join;
import uk.ac.ebi.embl.api.entry.location.Location;
import uk.ac.ebi.embl.api.entry.location.LocationFactory;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.entry.qualifier.QualifierFactory;
import uk.ac.ebi.embl.api.entry.sequence.Sequence;
import uk.ac.ebi.embl.api.entry.sequence.SequenceFactory;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.dao.EntryDAOUtils;
import uk.ac.ebi.embl.api.validation.fixer.sequence.AssemblyLevelSequenceFix;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlanProperty;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocusTagPrefixCheckTest {

	private Entry entry;
	private FeatureFactory featureFactory;
	private LocusTagPrefixCheck check;
	private EntryDAOUtils entryDAOUtils;
	private EmblEntryValidationPlanProperty property;
	private EntryFactory entryFactory;

	@Before
	public void setUp() throws SQLException {
        ValidationMessageManager.addBundle(ValidationMessageManager.STANDARD_VALIDATION_BUNDLE);
		entryFactory = new EntryFactory();
		featureFactory = new FeatureFactory();
		entry = entryFactory.createEntry();
		check = new LocusTagPrefixCheck();
        entryDAOUtils=createMock(EntryDAOUtils.class);
		property=new EmblEntryValidationPlanProperty();
		check.setEmblEntryValidationPlanProperty(property);
     }

	@Test
	public void testCheck_NoEntry() throws ValidationEngineException {
        ValidationResult result = check.check(null);
        assertTrue(result.isValid());
        assertEquals(0, result.getMessages().size());
	}

	@Test
	public void testCheck_NoFeatures() throws ValidationEngineException {
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertEquals(0, result.getMessages().size());
	}

	@Test
	public void testCheck_NoLocusTags() throws ValidationEngineException {
        Feature feature = featureFactory.createFeature("feature");
        Feature feature2 = featureFactory.createFeature("feature2");
        entry.addFeature(feature);
        entry.addFeature(feature2);
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertEquals(0, result.getMessages().size());
	}

    @Test
    public void testCheck_validLocusTagPrefix() throws SQLException, ValidationEngineException {
    	
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        HashSet<String> projectLocusTagPrefixes=new HashSet<String>();
        projectLocusTagPrefixes.add("BC03BB108");
        projectLocusTagPrefixes.add("BC03BB107");
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(projectLocusTagPrefixes);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertTrue(result.getMessages().size()==0);

    }

    @Test
    public void testCheck_inValidLocusTagPrefix() throws SQLException, ValidationEngineException {
    	
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        HashSet<String> projectLocusTagPrefixes=new HashSet<String>();
        projectLocusTagPrefixes.add("BC03");
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(projectLocusTagPrefixes);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
        ValidationResult result = check.check(entry);
        assertTrue(!result.isValid());
        assertEquals(1, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }

    @Test
    public void testCheck_notRegisteredLocusTagPrefix() throws SQLException, ValidationEngineException {
    	
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(new HashSet<String>());
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
        ValidationResult result = check.check(entry);
        assertTrue(!result.isValid());
        assertEquals(1, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }

    @Test
    public void testCheck_sampleIDLocusTagPrefix() throws SQLException, ValidationEngineException {
    	
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"SAMN02436291_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        entry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(new HashSet<String>());
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertEquals(0, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }

    @Test
    public void testCheck_assemblyvalidLocusTagPrefix() throws SQLException, ValidationEngineException {
    	Entry masterEntry =entryFactory.createEntry();
    	masterEntry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
    	masterEntry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
    	property.analysis_id.set("ERZ00001");
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        HashSet<String> projectLocusTagPrefixes=new HashSet<String>();
        projectLocusTagPrefixes.add("BC03BB108");
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(projectLocusTagPrefixes);
		expect(entryDAOUtils.getMasterEntry("ERZ00001")).andReturn(masterEntry);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
		check.setEmblEntryValidationPlanProperty(property);
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertTrue(result.getMessages().size()==0);

    }

    @Test
    public void testCheck_assemblyinValidLocusTagPrefix() throws SQLException, ValidationEngineException {
    	Entry masterEntry =entryFactory.createEntry();
    	masterEntry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
    	masterEntry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
    	property.analysis_id.set("ERZ00001");
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        HashSet<String> projectLocusTagPrefixes=new HashSet<String>();
        projectLocusTagPrefixes.add("BC03");
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(projectLocusTagPrefixes);
		expect(entryDAOUtils.getMasterEntry("ERZ00001")).andReturn(masterEntry);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
		check.setEmblEntryValidationPlanProperty(property);
        ValidationResult result = check.check(entry);
        assertTrue(!result.isValid());
        assertEquals(1, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }

    @Test
    public void testCheck_assemblynotRegisteredLocusTagPrefix() throws SQLException, ValidationEngineException {
    	Entry masterEntry =entryFactory.createEntry();
    	masterEntry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
    	masterEntry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
    	property.analysis_id.set("ERZ00001");
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"BC03BB108_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(new HashSet<String>());
		expect(entryDAOUtils.getMasterEntry("ERZ00001")).andReturn(masterEntry);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
		check.setEmblEntryValidationPlanProperty(property);
        ValidationResult result = check.check(entry);
        assertTrue(!result.isValid());
        assertEquals(1, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }

    @Test
    public void testCheck_assemblysampleIDLocusTagPrefix() throws SQLException, ValidationEngineException {
    	Entry masterEntry =entryFactory.createEntry();
    	masterEntry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
    	masterEntry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
    	property.analysis_id.set("ERZ00001");
    	Feature feature=featureFactory.createFeature("feature");
        feature.addQualifier(Qualifier.LOCUS_TAG_QUALIFIER_NAME,"SAMN02436291_2133");
        entry.addFeature(feature);
        entry.getProjectAccessions().add(new Text(new String("PRJNA19959")));
        entry.getXRefs().add(new XRef("BioSample", "SAMN02436291"));
		expect(entryDAOUtils.getProjectLocutagPrefix("PRJNA19959")).andReturn(new HashSet<String>());
		expect(entryDAOUtils.getMasterEntry("ERZ00001")).andReturn(masterEntry);
		replay(entryDAOUtils);
		check.setEntryDAOUtils(entryDAOUtils);
		check.setEmblEntryValidationPlanProperty(property);
        ValidationResult result = check.check(entry);
        assertTrue(result.isValid());
        assertEquals(0, result.count(LocusTagPrefixCheck.MESSAGE_ID_INVALID_PREFIX, Severity.ERROR));
    }
}
