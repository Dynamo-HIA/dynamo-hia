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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestSimulation {
	Log log = LogFactory.getLog(getClass().getName());

	File testFileOK = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestOK.xml");

	File longSimOutput = new File(
	"C:/eclipse321/workspace/CZM/data/longitudinalSimulationOutput.xml");

	File transSimOutput = new File(
	"C:/eclipse321/workspace/CZM/data/transversalSimulationOutput.xml");


	static public class UpdateRuleOneOne extends OneToOneUpdateRuleBase {
		public UpdateRuleOneOne() {
			super(1, 1);
		}

		@Override
		public int update(int currentValue) {
			return currentValue + 1;
		}
	}

	static public class UpdateRuleTwoTwo extends OneToOneUpdateRuleBase {
		public UpdateRuleTwoTwo() {
			super(2, 2);
		}

		@Override
		public int updateSelf(int currentValue) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	static public class UpdateRuleSixFour extends OneToOneUpdateRuleBase {
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
		int numberOfSteps = 10;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMapSingleton charConfMap = CharacteristicsConfigurationMapSingleton.getInstance();
		Characteristic characteristic1 = new Characteristic(1, "Eerste ziekte");
		Characteristic characteristic2 = new Characteristic(2, "Tweede ziekte");
		charConfMap.putCharacteristic(characteristic1);
		charConfMap.putCharacteristic(characteristic2);
		simulation.setCharacteristics(charConfMap);
		try {
			DOMBootStrap domBoot = new DOMBootStrap();
			Population population = domBoot.process2PopulationTree(testFileOK, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRuleRepository updateRuleStorage = new UpdateRuleRepository();
			updateRuleStorage.addUpdateRule(new UpdateRuleOneOne());
			updateRuleStorage.addUpdateRule(new UpdateRuleTwoTwo());
			updateRuleStorage.addUpdateRule(new UpdateRuleSixFour());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.isConfigurationOK());
			log.fatal("Running longitudinal.");
			simulation.run();
			log.fatal("Longitudinal run complete.");
			DOMPopulationWriter.writeToXMLFile(simulation.getPopulation(), numberOfSteps, longSimOutput);
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
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CDMRunException e) {
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
		int numberOfSteps = 10;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMapSingleton charConfMap = CharacteristicsConfigurationMapSingleton.getInstance();
		Characteristic characteristic1 = new Characteristic(1, "Eerste ziekte");
		Characteristic characteristic2 = new Characteristic(2, "Tweede ziekte");
		charConfMap.putCharacteristic(characteristic1);
		charConfMap.putCharacteristic(characteristic2);
		simulation.setCharacteristics(charConfMap);
		try {
			DOMBootStrap domBoot = new DOMBootStrap();
			Population population = domBoot.process2PopulationTree(testFileOK, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRuleRepository updateRuleStorage = new UpdateRuleRepository();
			updateRuleStorage.addUpdateRule(new UpdateRuleOneOne());
			updateRuleStorage.addUpdateRule(new UpdateRuleTwoTwo());
			updateRuleStorage.addUpdateRule(new UpdateRuleSixFour());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.isConfigurationOK());
			log.fatal("Running transversal");
//			simulation.runTransversal();
			simulation.setRunMode("transversal");
			simulation.run();
// ~
			log.fatal("Transversal run complete");
			DOMPopulationWriter.writeToXMLFile(simulation.getPopulation(), numberOfSteps, transSimOutput);
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
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (CDMRunException e) {
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
				nl.rivm.emi.cdm.rules.update.containment.test.TestUpdateRuleContainers.class);
	}

}
