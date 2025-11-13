package nl.rivm.emi.cdm.characteristic.types.test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicFromXMLFactory;
import nl.rivm.emi.cdm.characteristic.types.NumericalContinuousCharacteristicType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestNumericalContinuousCharacteristicType {
	Log log = LogFactory.getLog(getClass().getName());

	File continuousTypeCharacteristicFileNoLims = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/charconf_1conttypenolimits.xml");

	File continuousTypeCharacteristicFileTwoLims = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/charconf_1conttypetwolimits.xml");

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
		charType.setLimits(Float.valueOf(0F), Float.valueOf(3.14F));
		assertTrue(charType.isValueValid("1.2"));
		assertFalse(charType.isValueValid("42"));
	}

	@Test
	public void numericalContinuousSomeMoreValueHandling() {
		NumericalContinuousCharacteristicType charType = new NumericalContinuousCharacteristicType();
		assertFalse(charType.isCategoricalType());
		charType.setLimits(2F, 5F);
		assertFalse(charType.isValueValid("1"));
		assertTrue(charType.isValueValid("2"));
		assertFalse(charType.isValueValid("14"));
		// Default
		charType.setLimits(null, null);
		assertTrue(charType.isValueValid("-20"));
		assertTrue(charType.isValueValid("42"));
	}

	@Test
	public void noLimitsFromXML() {
		try {
			HierarchicalConfiguration characteristicConfiguration = null;
			if (continuousTypeCharacteristicFileNoLims.exists()) {
				characteristicConfiguration = new XMLConfiguration(
						continuousTypeCharacteristicFileNoLims);
			} else {
				log.error(String.format(
						"Configuration file %1$s does not exist",
						continuousTypeCharacteristicFileNoLims));
				assertTrue(false); // Force error.
			}

			Characteristic characteristic = CharacteristicFromXMLFactory
					.manufacture(characteristicConfiguration);
			assertTrue(characteristic.getType() instanceof NumericalContinuousCharacteristicType);
			NumericalContinuousCharacteristicType charType = (NumericalContinuousCharacteristicType) characteristic
					.getType();
			assertFalse(charType.isCategoricalType());
			charType.setLimits(2F, 5F);
			assertFalse(charType.isValueValid("1"));
			assertTrue(charType.isValueValid("2"));
			assertFalse(charType.isValueValid("14"));
			// Default
			charType.setLimits(null, null);
			assertTrue(charType.isValueValid("-20"));
			assertTrue(charType.isValueValid("42"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void twoLimitsFromXML() {
		try {
			HierarchicalConfiguration characteristicConfiguration = null;
			if (continuousTypeCharacteristicFileTwoLims.exists()) {
				characteristicConfiguration = new XMLConfiguration(
						continuousTypeCharacteristicFileTwoLims);
			} else {
				log.error(String.format(
						"Configuration file %1$s does not exist",
						continuousTypeCharacteristicFileTwoLims));
				assertTrue(false); // Force error.
			}

			Characteristic characteristic = CharacteristicFromXMLFactory
					.manufacture(characteristicConfiguration);
			assertTrue(characteristic.getType() instanceof NumericalContinuousCharacteristicType);
			NumericalContinuousCharacteristicType charType = (NumericalContinuousCharacteristicType) characteristic
					.getType();
			assertFalse(charType.isCategoricalType());
			charType.setLimits(2F, 5F);
			assertFalse(charType.isValueValid("1"));
			assertTrue(charType.isValueValid("2"));
			assertFalse(charType.isValueValid("14"));
			// Default
			charType.setLimits(null, null);
			assertTrue(charType.isValueValid("-20"));
			assertTrue(charType.isValueValid("42"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
