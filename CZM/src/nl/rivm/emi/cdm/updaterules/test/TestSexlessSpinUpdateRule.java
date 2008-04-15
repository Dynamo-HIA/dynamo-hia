package nl.rivm.emi.cdm.updaterules.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.WrongUpdateRuleException;
import nl.rivm.emi.cdm.updaterules.AgeOneToOneUpdateRule;
import nl.rivm.emi.cdm.updaterules.SpinSoloUpdateRule;
import nl.rivm.emi.cdm.updaterules.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.updaterules.containment.UpdateRuleRepository;
import nl.rivm.emi.cdm.updaterules.containment.UpdateRulesByCharIdRepository;
import nl.rivm.emi.cdm.updaterules.obsolete.AbstractDoubleBoundUpdateRule;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSexlessSpinUpdateRule {
	Log log = LogFactory.getLog(getClass().getName());

	File spinLeafOnlyConfiguration = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/spinleafonly.xml");

	File spinConfiguration1 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/spinconf1.xml");

	File spinConfiguration2 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/spinconf2.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void configurationTransitionMatrixLeaf() {
		SpinSoloUpdateRule daRule = new SpinSoloUpdateRule();
		assertNotNull(daRule);
		try {
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					spinLeafOnlyConfiguration);
			List<SubnodeConfiguration> snConf = configurationFileConfiguration
					.configurationsAt("leaf");
			Float leafValue = daRule.handleLeaf(snConf.get(0));
			assertEquals(new Float(.2F), leafValue);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}

	}

	@Test
	public void configurationInconsistentTransitionMatrix() {
		SpinSoloUpdateRule daRule = new SpinSoloUpdateRule();
		assertNotNull(daRule);
		try {
			assertFalse(daRule.loadConfigurationFile(spinConfiguration1));
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void configurationConsistentTransitionMatrix() {
		SpinSoloUpdateRule daRule = new SpinSoloUpdateRule();
		assertNotNull(daRule);
		try {
			assertTrue(daRule.loadConfigurationFile(spinConfiguration2));
			System.out.print(daRule
					.dumpTreeMapTree(daRule.transitionConfiguration));
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void previousPlusSomeUpdates() {
		SpinSoloUpdateRule daRule = new SpinSoloUpdateRule();
		assertNotNull(daRule);
		try {
			assertTrue(daRule.loadConfigurationFile(spinConfiguration2));
			long nextSeed = daRule.setAndNextSeed(1234567L);
			int oldValue = 3;
			for (int count = 0; count < 100; count++) {
				int newValue = ((Integer) daRule.update(new Integer(oldValue)))
						.intValue();
				System.out.println("Oldvalue " + oldValue
						+ " updated to newValue " + newValue);
				oldValue = newValue;
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.updaterules.test.TestSexlessSpinUpdateRule.class);
	}
}
