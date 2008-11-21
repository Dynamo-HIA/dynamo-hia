package nl.rivm.emi.cdm.iterations.one.test;

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
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
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

public class Iter1Test1_20 {
	Log log = LogFactory.getLog(getClass().getName());

	File testpop3a = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/testpop3c.xml");

	File longSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/longtestpop3c.xml");

	File transSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop3c.xml");

	File transSimOutputStep5 = new File(
	"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop3cst5.xml");

	static public class UpdateRuleST1_01 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_01() {
			super();
		}


		@Override
		public Object update(Object currentValue) throws CDMUpdateRuleException {
			int newValue = ((Number)currentValue).intValue();
			if (newValue > 10) {
				newValue = 10;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_02 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_02() {
			super();
		}

		@Override
		public Object update(Object currentValue) throws CDMUpdateRuleException {
			int newValue = 10;
			if (currentValue < 10) {
				newValue = currentValue + 1;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_03 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_03() {
			super(3, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = 10;
			if (currentValue < 10) {
				newValue = currentValue + 2;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_04 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_04() {
			super(4, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = 10;
			if (currentValue < 10) {
				newValue = currentValue * 2;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_05 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_05() {
			super(5, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = 10;
			if (currentValue < 10) {
				newValue = currentValue * 2 -1;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_06 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_06() {
			super(6, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = 1;
			if (currentValue < 1) {
				newValue = currentValue * 2 - 2;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_07 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_07() {
			super(7, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			return 1;
		}
	}

	static public class UpdateRuleST1_08 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_08() {
			super(8, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			return 0;
		}
	}

	static public class UpdateRuleST1_09 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_09() {
			super(9, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = currentValue * currentValue;
			if (newValue > 10) {
				newValue = 10;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_10 extends OneToOneUpdateRuleBase {
		public UpdateRuleST1_10() {
			super(10, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = currentValue - 1;
			if (newValue < 1) {
				newValue = 1;
			}
			return newValue;
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
			Population population = domBoot.process2PopulationTree(testpop3a, numberOfSteps);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRuleRepository updateRuleStorage = new UpdateRuleRepository();
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_01());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_02());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_03());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_04());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_05());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_06());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_07());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_08());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_09());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_10());
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
			Population population = domBoot.process2PopulationTree(testpop3a, numberOfSteps);
			simulation.setPopulation(population);
			int stepSize = 1;
			simulation.setTimeStep(stepSize);
			UpdateRuleRepository updateRuleStorage = new UpdateRuleRepository();
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_01());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_02());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_03());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_04());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_05());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_06());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_07());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_08());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_09());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_10());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.isConfigurationOK());
			log.fatal("Running transversal");
//			simulation.runTransversal();
			simulation.setRunMode("transversal");
			simulation.run();
// ~
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
