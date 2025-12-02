package nl.rivm.emi.cdm.rules.update.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.SexUpdateRuleEntryLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSexAndSpinUpdateRule {
	Log log = LogFactory.getLog(getClass().getName());

	File spinConfPlusSexConfiguration = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/spinconfplussex.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/charconf02_01.xml";
	@Before
	public void setup() throws ConfigurationException {
		String multipleCharacteristicsFileName = existingFileName_MultiChar;
		System.out.println(multipleCharacteristicsFileName);
		File multipleCharacteristicsFile = new File(
				multipleCharacteristicsFileName);
		@SuppressWarnings("unused")
		CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
				multipleCharacteristicsFile);
	}


	@After
	public void teardown() {
	}

	@Test
	public void spinConfPlusSex() throws CDMUpdateRuleException {
		SexUpdateRuleEntryLayer daRule = new SexUpdateRuleEntryLayer();
		assertNotNull(daRule);
		try {
			assertTrue(daRule
					.loadConfigurationFile(spinConfPlusSexConfiguration));
			long nextSeed = daRule.setAndNextSeed(1234567L);
			int oldValue = 3;
			for (int sexCount = 1; sexCount <= 2; sexCount++) {
				for (int count = 0; count < 100; count++) {
					Object[] parameters = {null, Integer.valueOf(sexCount),null, Integer.valueOf(oldValue).intValue()};
					int newValue = ((Integer) daRule.update(parameters, nextSeed));
					System.out.println("Sex " + sexCount + " timestep " + count + " oldvalue " + oldValue
							+ " updated to newValue " + newValue);
					oldValue = newValue;
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.rules.update.test.TestSexAndSpinUpdateRule.class);
	}
}
