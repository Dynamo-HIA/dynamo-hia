package nl.rivm.emi.cdm.simulation.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRuleRepository;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestSimulationFromXMLFactory {
	Log log = LogFactory.getLog(getClass().getName());

	File simulationConfiguration1 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/simulation1.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void loadConfiguration1() {
		HierarchicalConfiguration simulationConfiguration;
		try {
			if (simulationConfiguration1.exists()) {
				simulationConfiguration = new XMLConfiguration(
						simulationConfiguration1);
				Simulation sim1 = SimulationFromXMLFactory
						.manufacture_DOMPopulationTree(simulationConfiguration);
			} else {
				throw new ConfigurationException(String.format(
						"Configuration file %1$s does not exist",
						simulationConfiguration1));
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConversionException e1) {
			e1.printStackTrace();
			assertNull(e1); // Force error.

		} 	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.simulation.test.TestSimulationFromXMLFactory.class);
	}

}
