package nl.rivm.emi.cdm.characteristic.types.test;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.StringCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.WrongPossibleValueException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCharacteristicValues {
	Log logger = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void stringCategoricalPolymorphism_Filling() {
		AbstractCategoricalCharacteristicType charType = new StringCategoricalCharacteristicType();
		int correctCount = 0;
		if (charType.addPossibleValue((Object)"Aap")) {
			correctCount++;
		}
		if (charType.addPossibleValue("Noot")) {
			correctCount++;
		}
		if (charType.addPossibleValue("Mies")) {
			correctCount++;
		}
		if (charType.addPossibleValue(new Integer(1))) {
			correctCount++;
		}
		assertEquals(3, correctCount);
	}

	@Test
	// Another level of polymorphism.
	public void stringCategoricalPolymorphism_Filling2() {
		AbstractCategoricalCharacteristicType charType = new StringCategoricalCharacteristicType();
		charType.addPossibleValue("Aap");
		charType.addPossibleValue("Noot");
		charType.addPossibleValue("Mies");
		charType.addPossibleValue(new Integer(1));
		assertEquals(3, charType.getNumberOfPossibleValues());
	}

	@Test
	public void stringCategoricalPolymorphism_Getting() {
		AbstractCategoricalCharacteristicType charType = new StringCategoricalCharacteristicType();
		charType.addPossibleValue("Aap");
		charType.addPossibleValue("Noot");
		charType.addPossibleValue("Mies");
		String pValue = (String) charType.getPossibleValue(1);
		assertEquals("Noot", pValue);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
