package nl.rivm.emi.dynamo.data.types.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.PopulationSizePerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.data.types.Age;
import nl.rivm.emi.dynamo.data.types.Probability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTypes {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testAge() {
		Float minusTwoFloat = new Float(-2F);
		Float fourFloat = new Float(4F);
		Float oneOhOneFloat = new Float(101F);
		assertFalse(Age.inRange(minusTwoFloat));
		assertTrue(Age.inRange(fourFloat));
		assertFalse(Age.inRange(oneOhOneFloat));
	}

	@Test
	public void testProbability() {
		Float minusTwoFloat = new Float(-2F);
		Float pointFourFloat = new Float(.4F);
		Float oneOhOneFloat = new Float(101F);
		assertFalse(Probability.inRange(minusTwoFloat));
		assertTrue(Probability.inRange(pointFourFloat));
		assertFalse(Probability.inRange(oneOhOneFloat));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.types.test.TestTypes.class);
	}
}
