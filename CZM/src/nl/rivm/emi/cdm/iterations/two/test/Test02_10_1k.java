package nl.rivm.emi.cdm.iterations.two.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.population.PopulationWriter;
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

public class Test02_10_1k {
	Log log = LogFactory.getLog(getClass().getName());

	String preCharConfig = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/charconf02_10.xml";

	File simulationConfigurationFile = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/simulation02_10_1k.xml");

	File simOutput = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/sim02_10_1k_out.xml");

	HierarchicalConfiguration simulationConfiguration;


	Simulation sim;

	@Before
		public void setup() throws ConfigurationException {
			System.out.println(preCharConfig);
			try {
				File multipleCharacteristicsFile = new File(
						preCharConfig);
				CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
						multipleCharacteristicsFile);
				CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
						.getInstance();
				if (simulationConfigurationFile.exists()) {
					simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					sim = SimulationFromXMLFactory
							.manufacture(simulationConfiguration);
				} else {
					throw new ConfigurationException(String.format(
							"Configuration file %1$s does not exist",
							simulationConfigurationFile.getAbsolutePath()));
				}
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (ConversionException e1) {
				e1.printStackTrace();
				assertNull(e1); // Force error.
			}
		}

	@After
	public void teardown() {
	}

	@Test
	public void runSimulation() {
		assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
				.size() > 1);
		try {
			log.fatal("Starting run.");
			sim.run();
			log.fatal("Run complete.");
			PopulationWriter.writeToXMLFile(sim.getPopulation(), sim
					.getStepsInRun(), simOutput);
			log.fatal("Result written.");
		} catch (CDMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.iterations.two.test.Test02_10_1k.class);
	}
}