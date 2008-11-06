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
import nl.rivm.emi.dynamo.data.factories.notinuse.SomethingPerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;

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
		Age utilityAge = new Age();
		Integer minusTwoInteger = new Integer(-2);
		Integer fourInteger = new Integer(4);
		Integer oneOhOneInteger = new Integer(101);
		assertFalse(utilityAge.inRange(minusTwoInteger));
		assertTrue(utilityAge.inRange(fourInteger));
		assertFalse(utilityAge.inRange(oneOhOneInteger));
	}

	@Test
	public void testProbability() {
		Probability utilityProbability = new Probability();
		Float minusTwoFloat = new Float(-2F);
		Float pointFourFloat = new Float(.4F);
		Float oneOhOneFloat = new Float(101F);
		assertFalse(utilityProbability.inRange(minusTwoFloat));
		assertTrue(utilityProbability.inRange(pointFourFloat));
		assertFalse(utilityProbability.inRange(oneOhOneFloat));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.types.test.TestTypes.class);
	}
}
