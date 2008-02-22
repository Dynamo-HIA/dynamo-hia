package nl.rivm.emi.cdm.simulation.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMap;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFactory;
import nl.rivm.emi.cdm.population.PopulationWriter;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.updating.UpdateRuleBaseClass;
import nl.rivm.emi.cdm.updating.UpdateRuleStorage;
import nl.rivm.emi.cdm.updating.UpdateRulesByCharIdContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TestSimulation1M {
	Log log = LogFactory.getLog(getClass().getName());

	File testFileOK = new File(
			"C:/eclipse321/workspace/CZM/data/sim1meg_population.xml");

	File longSimOutput = new File(
	"C:/eclipse321/workspace/CZM/data/longitudinal1MSimulationOutput.xml");

	File transSimOutput = new File(
	"C:/eclipse321/workspace/CZM/data/transversal1MSimulationOutput.xml");


	static public class UpdateRuleOneOne extends UpdateRuleBaseClass {
		public UpdateRuleOneOne() {
			super(1, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			return currentValue + 1;
		}
	}

	static public class UpdateRuleTwoTwo extends UpdateRuleBaseClass {
		public UpdateRuleTwoTwo() {
			super(2, 2);
		}

		@Override
		public int updateSelf(int currentValue) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	static public class UpdateRuleSixFour extends UpdateRuleBaseClass {
		public UpdateRuleSixFour() {
			super(6, 4);
		}

		@Override
		public int updateSelf(int currentValue) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void runLongitudinalSimulation() {
		log.fatal("Starting longitudinal");
		String label = "Checking";
		int numberOfSteps = 1000000;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMap charConfMap = new CharacteristicsConfigurationMap();
		Characteristic characteristic1 = new Characteristic(1, "Eerste ziekte");
		Characteristic characteristic2 = new Characteristic(2, "Tweede ziekte");
		charConfMap.addCharacteristic(characteristic1);
		charConfMap.addCharacteristic(characteristic2);
		simulation.setCharacteristics(charConfMap);
		try {
			DOMBootStrap domBoot = new DOMBootStrap();
			Population population = domBoot.process2PopulationTree(testFileOK, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setStepSize(stepSize);
			UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();
			updateRuleStorage.addUpdateRule(new UpdateRuleOneOne());
			updateRuleStorage.addUpdateRule(new UpdateRuleTwoTwo());
			updateRuleStorage.addUpdateRule(new UpdateRuleSixFour());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.sanityCheck());
			log.fatal("Running longitudinal.");
			simulation.runLongitudinal();
			log.fatal("Longitudinal run complete.");
			PopulationWriter.writeToXMLFile(simulation.getPopulation(), numberOfSteps, longSimOutput);
			log.fatal("Longitudinal result written.");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CZMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CZMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void runTransversalSimulation() {
		log.fatal("Starting transversal");
		String label = "Checking";
		int numberOfSteps = 1000000;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMap charConfMap = new CharacteristicsConfigurationMap();
		Characteristic characteristic1 = new Characteristic(1, "Eerste ziekte");
		Characteristic characteristic2 = new Characteristic(2, "Tweede ziekte");
		charConfMap.addCharacteristic(characteristic1);
		charConfMap.addCharacteristic(characteristic2);
		simulation.setCharacteristics(charConfMap);
		try {
			DOMBootStrap domBoot = new DOMBootStrap();
			Population population = domBoot.process2PopulationTree(testFileOK, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setStepSize(stepSize);
			UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();
			updateRuleStorage.addUpdateRule(new UpdateRuleOneOne());
			updateRuleStorage.addUpdateRule(new UpdateRuleTwoTwo());
			updateRuleStorage.addUpdateRule(new UpdateRuleSixFour());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.sanityCheck());
			log.fatal("Running transversal");
			simulation.runTransversal();
			log.fatal("Transversal run complete");
			PopulationWriter.writeToXMLFile(simulation.getPopulation(), numberOfSteps, transSimOutput);
			log.fatal("Transversal result written.");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CZMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CZMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.updating.test.TestUpdateRuleContainers.class);
	}

}
