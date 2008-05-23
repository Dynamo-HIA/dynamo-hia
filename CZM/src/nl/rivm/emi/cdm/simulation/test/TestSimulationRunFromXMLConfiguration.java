package nl.rivm.emi.cdm.simulation.test;

import static org.junit.Assert.assertEquals;
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
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.cdm.updaterules.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.updaterules.containment.UpdateRuleRepository;

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

public class TestSimulationRunFromXMLConfiguration {
	Log log = LogFactory.getLog(getClass().getName());

	File simulationConfiguration1 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/simulation1.xml");

	File sim1Output = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/sim1output.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/charconf1.xml";

	HierarchicalConfiguration simulationConfiguration;

	Simulation sim1;

	Simulation sim2;

	@Before
	public void setup() throws ConfigurationException {
		String multipleCharacteristicsFileName = existingFileName_MultiChar;
		System.out.println(multipleCharacteristicsFileName);
		File multipleCharacteristicsFile = new File(
				multipleCharacteristicsFileName);
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

//	@Test
//	public void runSimulation2() {
//		assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
//				.size() > 1);
//		try {
//			log.fatal("Starting run.");
//			sim2.run();
//			log.fatal("Run complete.");
//			PopulationWriter.writeToXMLFile(sim2.getPopulation(), sim2
//					.getStepsInRun(), sim2Output);
//			log.fatal("Result written.");
//		} catch (CDMRunException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		}
//	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.simulation.test.TestSimulationRunFromXMLConfiguration.class);
	}

}
