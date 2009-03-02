package nl.rivm.emi.dynamo.data.types.atomic.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.Value;

import org.apache.commons.configuration.ConfigurationException;
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
		try {
			Age age;
			age = new Age();
			assertNotNull(age);
			assertTrue(age.inRange(new Integer(0)));
			assertFalse(age.inRange(-1));
			assertTrue(age.inRange(95));
			assertFalse(age.inRange(96));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void testSex() {
		try {
			Sex sex;
			sex = new Sex();
			assertNotNull(sex);
			assertFalse(sex.inRange(-1));
			assertTrue(sex.inRange(new Integer(0)));
			assertTrue(sex.inRange(1));
			assertFalse(sex.inRange(2));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void testPercentage() {
		try {
			Percent percentage;
			percentage = new Percent();
			assertNotNull(percentage);
			assertFalse(percentage.inRange(-1F));
			assertTrue(percentage.inRange(new Float(0)));
			assertTrue(percentage.inRange(100F));
			assertFalse(percentage.inRange(101F));
			assertFalse(Percent.matchPattern.matcher("3333").matches());
			assertTrue(Percent.matchPattern.matcher(".").matches());
			assertTrue(Percent.matchPattern.matcher("0.123").matches());
			assertTrue(Percent.matchPattern.matcher("999.999").matches());
			assertFalse(Percent.matchPattern.matcher("9999.999").matches());
			assertFalse(Percent.matchPattern.matcher("9.999999999").matches());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void testProbability() {
		try {
			Probability probability;
			probability = new Probability();
			assertNotNull(probability);
			assertFalse(probability.inRange(-1F));
			assertTrue(probability.inRange(0F));
			assertTrue(probability.inRange(0.77F));
			assertFalse(probability.inRange(1.01F));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void testStandardValue() {
		try {
			Value standardValue;
			standardValue = new Value();
			assertNotNull(standardValue);
			assertFalse(standardValue.inRange(-1F));
			assertTrue(standardValue.inRange(0F));
			assertTrue(standardValue.inRange(Float.MAX_VALUE));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.types.atomic.test.TestAtomicTypes.class);
	}
}
