package nl.rivm.emi.cdm.iterations.one.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.rules.update.AbstractDoubleBoundOneToOneUpdateRule;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRules4Simulation;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class Iter1Test1_01 {
	Log log = LogFactory.getLog(getClass().getName());

	File testpop1 = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/testpop1.xml");

	File longSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/longtestpop1.xml");

	File transSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop1.xml");

	File transSimOutputStep5 = new File(
	"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop1st5.xml");

	@SuppressWarnings("deprecation")
	static public class UpdateRuleST1_01 extends AbstractDoubleBoundOneToOneUpdateRule {

	
		public UpdateRuleST1_01() {
			super(1, 1);
		}

		@Override
		public Object update(Object currentValue) {
			return currentValue;
		}

		public int getCharacteristicId() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setCharacteristicId(int characteristicId) {
			// TODO Auto-generated method stub
			
		}

		public float getStepSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setStepSize(float stepSize) {
			// TODO Auto-generated method stub
			
		}
	}

	@SuppressWarnings("deprecation")
	static public class UpdateRuleST1_02 extends AbstractDoubleBoundOneToOneUpdateRule {

		
		public UpdateRuleST1_02() {
			super(2, 1);
		}

		@Override
		public Object update(Object currentValue) {
			int newValue = 10;
			int currentIntValue = ((Integer)currentValue).intValue();
			if (currentIntValue < 10) {
				newValue = currentIntValue + 1;
			}
			return newValue;
		}

public int getCharacteristicId() {
	// TODO Auto-generated method stub
	return 0;
}

public void setCharacteristicId(int characteristicId) {
	// TODO Auto-generated method stub
	
}

public float getStepSize() {
	// TODO Auto-generated method stub
	return 0;
}

public void setStepSize(float stepSize) {
	// TODO Auto-generated method stub
	
}
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

//	@Test
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
			Population population = domBoot.process2PopulationTree(testpop1, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRules4Simulation updateRuleStorage = new UpdateRules4Simulation();
			updateRuleStorage.put(1,new UpdateRuleST1_01());
			updateRuleStorage.put(2,new UpdateRuleST1_02());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.isConfigurationOK());
			log.fatal("Running longitudinal.");
			simulation.run();
			log.fatal("Longitudinal run complete.");
			DOMPopulationWriter.writeToXMLFile(simulation.getPopulation(),
					numberOfSteps, longSimOutput);
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
		Characteristic characteristic3 = new Characteristic(3, "Tweede ziekte");
		Characteristic characteristic4 = new Characteristic(4, "Tweede ziekte");
		Characteristic characteristic5 = new Characteristic(5, "Tweede ziekte");
		Characteristic characteristic6 = new Characteristic(6, "Tweede ziekte");
		Characteristic characteristic7 = new Characteristic(7, "Tweede ziekte");
		Characteristic characteristic8 = new Characteristic(8, "Tweede ziekte");
		Characteristic characteristic9 = new Characteristic(9, "Tweede ziekte");
		Characteristic characteristic10 = new Characteristic(10, "Tweede ziekte");
		charConfMap.putCharacteristic(characteristic1);
		charConfMap.putCharacteristic(characteristic2);
		charConfMap.putCharacteristic(characteristic3);
		charConfMap.putCharacteristic(characteristic4);
		charConfMap.putCharacteristic(characteristic5);
		charConfMap.putCharacteristic(characteristic6);
		charConfMap.putCharacteristic(characteristic7);
		charConfMap.putCharacteristic(characteristic8);
		charConfMap.putCharacteristic(characteristic9);
		charConfMap.putCharacteristic(characteristic10);
		simulation.setCharacteristics(charConfMap);
		try {
			DOMBootStrap domBoot = new DOMBootStrap();
			Population population = domBoot.process2PopulationTree(testpop1, 1);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRules4Simulation updateRuleStorage = new UpdateRules4Simulation();
			updateRuleStorage.put(1, new UpdateRuleST1_01());
			updateRuleStorage.put(2, new UpdateRuleST1_02());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.isConfigurationOK());
			log.fatal("Running transversal");
			simulation.setRunMode("transversal");
			simulation.run();
			log.fatal("Transversal run complete");
			DOMPopulationWriter.writeToXMLFile(simulation.getPopulation(),
					numberOfSteps, transSimOutput);
			DOMPopulationWriter.writeToXMLFile(simulation.getPopulation(),
					5, transSimOutputStep5);
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
