package nl.rivm.emi.dynamo.data.types.atomic.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Percentage;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.RangeValueException;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.StandardValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAtomicTypes {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testAge() {
		Age age;
		age = new Age();
		assertNotNull(age);
		assertTrue(age.inRange(new Integer(0)));
		assertFalse(age.inRange(-1));
		assertTrue(age.inRange(95));
		assertFalse(age.inRange(96));
	}

	@Test
	public void testSex() {
		Sex sex;
		sex = new Sex();
		assertNotNull(sex);
		assertFalse(sex.inRange(-1));
		assertTrue(sex.inRange(new Integer(0)));
		assertTrue(sex.inRange(1));
		assertFalse(sex.inRange(2));
	}

	@Test
	public void testPercentage() {
		Percentage percentage;
		percentage = new Percentage();
		assertNotNull(percentage);
		assertFalse(percentage.inRange(-1));
		assertTrue(percentage.inRange(new Integer(0)));
		assertTrue(percentage.inRange(100));
		assertFalse(percentage.inRange(101));
	}

	@Test
	public void testProbability() {
		Probability probability;
		probability = new Probability();
		assertNotNull(probability);
		assertFalse(probability.inRange(-1F));
		assertTrue(probability.inRange(0F));
		assertTrue(probability.inRange(0.77F));
		assertFalse(probability.inRange(1.01F));
	}

	@Test
	public void testStandardValue() {
		StandardValue standardValue;
		standardValue = new StandardValue();
		assertNotNull(standardValue);
		assertFalse(standardValue.inRange(-1F));
		assertTrue(standardValue.inRange(0F));
		assertTrue(standardValue.inRange(Float.MAX_VALUE));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.types.atomic.test.TestAtomicTypes.class);
	}
}
