package nl.rivm.emi.cdm.characteristic.values.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueStringParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCharcteristicValueStringParser {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void parseWrongIntegerString1() {
		String inputString = "a";
		Integer result = CharacteristicValueStringParser.parseStringToInteger(inputString);
		assertNull(result);
	}

	@Test
	public void parseWrongIntegerString2() {
		String inputString = "1.1";
		Integer result = CharacteristicValueStringParser.parseStringToInteger(inputString);
		assertNull(result);
	}

	@Test
	public void parseWrongIntegerString3() {
		String inputString = "1,1";
		Integer result = CharacteristicValueStringParser.parseStringToInteger(inputString);
		assertNull(result);
	}

	@Test
	public void parseCorrectIntegerString() {
		String inputString = "11";
		Integer result = CharacteristicValueStringParser.parseStringToInteger(inputString);
		assertNotNull(result);
		assertEquals(new Integer(11), result);
	}

	@Test
	public void parseWrongFloatString1() {
		String inputString = "a";
		Float result = CharacteristicValueStringParser.parseStringToFloat(inputString);
		assertNull(result);
	}

	@Test
	public void parseWrongFloatString2() {
		String inputString = "1,2";
		Float result = CharacteristicValueStringParser.parseStringToFloat(inputString);
		assertNull(result);
	}

	@Test
	public void parseCorrectFloatString1() {
		String inputString = "1";
		Float result = CharacteristicValueStringParser.parseStringToFloat(inputString);
		assertNotNull(result);
		assertEquals(new Float(1), result);
	}

	@Test
	public void parseCorrectFloatString2() {
		String inputString = ".1";
		Float result = CharacteristicValueStringParser.parseStringToFloat(inputString);
		assertNotNull(result);
		assertEquals(new Float(.1), result);
	}

	@Test
	public void parseCorrectFloatString3() {
		String inputString = "11.12";
		Float result = CharacteristicValueStringParser.parseStringToFloat(inputString);
		assertNotNull(result);
		assertEquals(new Float(11.12), result);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(nl.rivm.emi.cdm.characteristic.values.test.TestCharcteristicValueStringParser.class);
	}
}
