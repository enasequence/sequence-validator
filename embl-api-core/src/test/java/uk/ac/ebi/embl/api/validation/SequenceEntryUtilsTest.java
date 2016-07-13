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
package uk.ac.ebi.embl.api.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.EntryFactory;
import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.api.entry.feature.FeatureFactory;
import uk.ac.ebi.embl.api.entry.location.Location;
import uk.ac.ebi.embl.api.entry.location.LocationFactory;
import uk.ac.ebi.embl.api.entry.location.Order;
import uk.ac.ebi.embl.api.entry.sequence.Sequence;
import uk.ac.ebi.embl.api.entry.sequence.SequenceFactory;
import uk.ac.ebi.embl.api.validation.SequenceEntryUtils;
import uk.ac.ebi.embl.api.validation.helper.Utils;

public class SequenceEntryUtilsTest {

	private EntryFactory entryFactory;
	private SequenceFactory sequenceFactory;
	private FeatureFactory featureFactory;
	private LocationFactory locationFactory;

	@Before
	public void setUp() throws Exception {
		entryFactory = new EntryFactory();
		sequenceFactory = new SequenceFactory();
		featureFactory = new FeatureFactory();
        locationFactory = new LocationFactory();
	}

	@Test
	public void testGetMoleculeType_Null() {
		assertNull(SequenceEntryUtils.getMoleculeType(null));
	}

	@Test
	public void testGetMoleculeType_NoSequence() {
		EntryFactory factory = new EntryFactory();
		Entry entry = factory.createEntry(); 
		assertNull(SequenceEntryUtils.getMoleculeType(entry));
	}

	@Test
	public void testGetMoleculeType_EmptySequence() {
		Entry entry = entryFactory.createEntry();
		entry.setSequence(sequenceFactory.createSequence());
		assertNull(SequenceEntryUtils.getMoleculeType(entry));
	}

	@Test
	public void testGetMoleculeType() {
		Entry entry = entryFactory.createEntry();
		Sequence sequence = sequenceFactory.createSequence();
		entry.setSequence(sequence);
		sequence.setMoleculeType("DNA");
		assertEquals("DNA", SequenceEntryUtils.getMoleculeType(entry));
	}

	@Test
	public void testIsFeatureAvailable_NullNull() {
		assertFalse(SequenceEntryUtils.isFeatureAvailable(null, null));
	}

	@Test
	public void testIsFeatureAvailable_NoEntry() {
		assertFalse(SequenceEntryUtils.isFeatureAvailable("feature", null));
	}

	@Test
	public void testIsFeatureAvailable_NoFeatureName() {
		Entry entry = entryFactory.createEntry();
		assertFalse(SequenceEntryUtils.isFeatureAvailable(null, entry));
	}

	@Test
	public void testIsFeatureAvailable_EmptyEntry() {
		Entry entry = entryFactory.createEntry();
		assertFalse(SequenceEntryUtils.isFeatureAvailable("feature", entry));
	}

	@Test
	public void testIsFeatureAvailable_FeatureNotExists() {
		Entry entry = entryFactory.createEntry();
		Feature feature = featureFactory.createFeature("feature");
		entry.addFeature(feature);
		assertFalse(SequenceEntryUtils.isFeatureAvailable("x", entry));
	}
	
	@Test
	public void testIsFeatureAvailable_FeatureExists() {
		Entry entry = entryFactory.createEntry();
		Feature feature = featureFactory.createFeature("feature");
		entry.addFeature(feature);
		assertTrue(SequenceEntryUtils.isFeatureAvailable("feature", entry));
	}
	
	@Test
	public void testIsFeatureAvailable_MultipleFeatureExists() {
		Entry entry = entryFactory.createEntry();
		entry.addFeature(featureFactory.createFeature("feature"));
		entry.addFeature(featureFactory.createFeature("feature"));
		assertTrue(SequenceEntryUtils.isFeatureAvailable("feature", entry));
	}

    @Test
	public void testIsFeatureWithin() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(5l,10l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(5l,10l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertTrue(SequenceEntryUtils.isLocationWithin(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    @Test
	public void testIsFeatureWithin2() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(5l,10l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(5l,9l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertFalse(SequenceEntryUtils.isLocationWithin(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    @Test
	public void testIsFeatureWithin3() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(4l,10l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(5l,10l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertFalse(SequenceEntryUtils.isLocationWithin(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    @Test
	public void testIsFeatureWithin4() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(4l,10l));
        order.addLocation(locationFactory.createLocalRange(15l,21l));
        order.addLocation(locationFactory.createLocalRange(22l,29l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(3l,10l));
        order2.addLocation(locationFactory.createLocalRange(15l,21l));
        order2.addLocation(locationFactory.createLocalRange(22l,30l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertTrue(SequenceEntryUtils.isLocationWithin(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    /**
     * simple locations where there is overlap
     */
    @Test
	public void testDoLocationsOverlap1() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(10l,20l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(5l,11l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertTrue(SequenceEntryUtils.doLocationsOverlap(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    /**
     * simple locations where there no overlap
     */
    @Test
	public void testDoLocationsOverlap2() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(10l,20l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(21l,25l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertFalse(SequenceEntryUtils.doLocationsOverlap(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    /**
     * simple locations where there no overlap due to strandedness
     */
    @Test
	public void testDoLocationsOverlap3() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(10l,20l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(10l,20l,true));//is complement so no overlap
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertFalse(SequenceEntryUtils.doLocationsOverlap(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    /**
     * segmented location where the segments of one are right inbetween the segments of the other - but dont overlap
     */
    @Test
	public void testDoLocationsOverlap4() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(1l,10l));
        order.addLocation(locationFactory.createLocalRange(20l,30l));
        order.addLocation(locationFactory.createLocalRange(40l,50l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(11l,19l));
        order2.addLocation(locationFactory.createLocalRange(31l,39l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertFalse(SequenceEntryUtils.doLocationsOverlap(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    /**
     * segmented location where the segments of one are right inbetween the segments of the other - and do overlap
     */
    @Test
	public void testDoLocationsOverlap5() {
        Feature innerFeature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(1l,10l));
        order.addLocation(locationFactory.createLocalRange(20l,30l));
        order.addLocation(locationFactory.createLocalRange(40l,50l));
        order.setComplement(false);
        innerFeature.setLocations(order);

        Feature outerFeature = featureFactory.createFeature("feature");
        Order<Location> order2 = new Order<Location>();
        order2.addLocation(locationFactory.createLocalRange(11l,20l));//overlaps
        order2.addLocation(locationFactory.createLocalRange(31l,39l));
        order2.setComplement(false);
        outerFeature.setLocations(order2);

        assertTrue(SequenceEntryUtils.doLocationsOverlap(innerFeature.getLocations(), outerFeature.getLocations()));
	}

    @Test
	public void testIsCircularBoundary() {
        long sequenceLength = 100;
        Feature feature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(5l, sequenceLength));
        order.addLocation(locationFactory.createLocalRange(1l,4l));
        feature.setLocations(order);

        assertTrue(SequenceEntryUtils.isCircularBoundary(feature.getLocations(), sequenceLength));
	}

    @Test
	public void testIsCircularBoundary2() {
        long sequenceLength = 100;
        Feature feature = featureFactory.createFeature("feature");
        Order<Location> order = new Order<Location>();
        order.addLocation(locationFactory.createLocalRange(5l, sequenceLength));
        feature.setLocations(order);

        assertFalse(SequenceEntryUtils.isCircularBoundary(feature.getLocations(), sequenceLength));
	}
    
   
}
