package nl.rivm.emi.cdm.characteristic.types.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.NumericalContinuousCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.StringCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.WrongPossibleValueException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestNumericalContinuousCharacteristicTYpeValues {
	Log logger = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void numericalContinuousValueHandling() {
		NumericalContinuousCharacteristicType charType = new NumericalContinuousCharacteristicType();
		assertFalse(charType.isValueValid("aap"));
		assertTrue(charType.isValueValid("1"));
		assertTrue(charType.isValueValid("1.2"));
		charType.setLimits(new Float(0F), new Float(3.14F));
		assertTrue(charType.isValueValid("1.2"));
		assertFalse(charType.isValueValid("42"));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
