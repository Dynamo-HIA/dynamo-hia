package nl.rivm.emi.cdm.iterations.two.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
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

public class Test02_01 {
	Log log = LogFactory.getLog(getClass().getName());

	File simulationConfiguration1 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/simulation02_01.xml");

	File sim1Output = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/sim02_01_out.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/charconf02_01.xml";

	HierarchicalConfiguration simulationConfiguration;

	Simulation sim1;

	Simulation sim2;

	@Before
	public void setup() throws ConfigurationException {
		String multipleCharacteristicsFileName = existingFileName_MultiChar;
		System.out.println(multipleCharacteristicsFileName);
		File multipleCharacteristicsFile = new File(
				multipleCharacteristicsFileName);
		@SuppressWarnings("unused")
		CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
				multipleCharacteristicsFile);
		try {
			if (simulationConfiguration1.exists()) {
				simulationConfiguration = new XMLConfiguration(
						simulationConfiguration1);
				sim1 = SimulationFromXMLFactory
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
		}
	}

	@After
	public void teardown() {
	}

	@Test
	public void runSimulation1() {
		assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
				.size() > 1);
		try {
			log.fatal("Starting run.");
			sim1.run();
			log.fatal("Run complete.");
			DOMPopulationWriter.writeToXMLFile(sim1.getPopulation(), sim1
					.getStepsInRun(), sim1Output);
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
				nl.rivm.emi.cdm.iterations.two.test.Test02_01.class);
	}

}
