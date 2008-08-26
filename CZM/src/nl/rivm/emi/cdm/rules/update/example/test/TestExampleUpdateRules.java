package nl.rivm.emi.cdm.rules.update.example.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.example.ExampleMultiToOneUpdateRule;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestExampleUpdateRules {
	Log log = LogFactory.getLog(getClass().getName());

	String exampleMultiToOneConfigFile = "C:/eclipse321/workspace/CZM/unittestdata/exampleupdaterules/config1.xml";

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void multiToOneUpdateRule() {
		try {
			ExampleMultiToOneUpdateRule exampleMultiToOneUpdateRule = new ExampleMultiToOneUpdateRule();
			assertNotNull(exampleMultiToOneUpdateRule);
			File configFile = new File(exampleMultiToOneConfigFile);
			assertNotNull(configFile);
			assertTrue(configFile.exists());
			assertTrue(configFile.isFile());
			assertTrue(configFile.canRead());
			assertTrue(exampleMultiToOneUpdateRule instanceof ConfigurationEntryPoint);
			exampleMultiToOneUpdateRule.loadConfigurationFile(configFile);
			Object[] currentValues = new Object[3];
			currentValues[0] = null;
			currentValues[1] = new Integer(5);
			currentValues[2] = new Integer(2);
			Object newValue = exampleMultiToOneUpdateRule.update(currentValues);
			assertEquals(42, newValue);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.rules.update.example.test.TestExampleUpdateRules.class);
	}
}
