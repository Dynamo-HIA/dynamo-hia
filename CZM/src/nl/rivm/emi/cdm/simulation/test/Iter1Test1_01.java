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

public class Iter1Test1_01 {
	Log log = LogFactory.getLog(getClass().getName());

	File testpop3a = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/testpop3d.xml");

	File longSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/longtestpop3d.xml");

	File transSimOutput = new File(
			"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop3d.xml");

	File transSimOutputStep5 = new File(
	"C:/eclipse321/workspace/CZM/data/iter1test1/transtestpop3dst5.xml");

	static public class UpdateRuleST1_01 extends UpdateRuleBaseClass {
		public UpdateRuleST1_01() {
			super(1, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
				int newValue = currentValue;
				if (newValue > 10) {
					newValue = 10;
				}
				return newValue;
		}
	}

	static public class UpdateRuleST1_02 extends UpdateRuleBaseClass {
		public UpdateRuleST1_02() {
			super(2, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			int newValue = 10;
			if (currentValue < 10) {
				newValue = currentValue + 1;
			}
			return newValue;
		}
	}

	static public class UpdateRuleST1_03 extends UpdateRuleBaseClass {
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

	static public class UpdateRuleST1_04 extends UpdateRuleBaseClass {
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

	static public class UpdateRuleST1_05 extends UpdateRuleBaseClass {
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

	static public class UpdateRuleST1_06 extends UpdateRuleBaseClass {
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

	static public class UpdateRuleST1_07 extends UpdateRuleBaseClass {
		public UpdateRuleST1_07() {
			super(7, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			return 1;
		}
	}

	static public class UpdateRuleST1_08 extends UpdateRuleBaseClass {
		public UpdateRuleST1_08() {
			super(8, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			return 0;
		}
	}

	static public class UpdateRuleST1_09 extends UpdateRuleBaseClass {
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

	static public class UpdateRuleST1_10 extends UpdateRuleBaseClass {
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

//	@Test
	public void runLongitudinalSimulation() {
		log.fatal("Starting longitudinal");
		String label = "Checking";
		int numberOfSteps = 10;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMap charConfMap = new CharacteristicsConfigurationMap();
		Characteristic characteristic1 = new Characteristic(1, "Eerste ziekte");
		Characteristic characteristic2 = new Characteristic(2, "Tweede ziekte");
		charConfMap.addCharacteristic(characteristic1);
		charConfMap.addCharacteristic(characteristic2);
		simulation.setCharacteristics(charConfMap);
		try {
			simulation.makeAndSetPopulation(testpop3a);
			int stepSize = 1;
			simulation.setStepSize(stepSize);
			UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_01());
			updateRuleStorage.addUpdateRule(new UpdateRuleST1_02());
			simulation.setUpdateRuleStorage(updateRuleStorage);
			assertTrue(simulation.sanityCheck());
			log.fatal("Running longitudinal.");
			simulation.runLongitudinal();
			log.fatal("Longitudinal run complete.");
			PopulationWriter.writeToXMLFile(simulation.getPopulation(),
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
		int numberOfSteps = 2;
		Simulation simulation = new Simulation(label, numberOfSteps);
		assertNotNull(simulation);
		CharacteristicsConfigurationMap charConfMap = new CharacteristicsConfigurationMap();
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
		charConfMap.addCharacteristic(characteristic1);
		charConfMap.addCharacteristic(characteristic2);
		charConfMap.addCharacteristic(characteristic3);
		charConfMap.addCharacteristic(characteristic4);
		charConfMap.addCharacteristic(characteristic5);
		charConfMap.addCharacteristic(characteristic6);
		charConfMap.addCharacteristic(characteristic7);
		charConfMap.addCharacteristic(characteristic8);
		charConfMap.addCharacteristic(characteristic9);
		charConfMap.addCharacteristic(characteristic10);
		simulation.setCharacteristics(charConfMap);
		try {
			simulation.makeAndSetPopulation(testpop3a);
			int stepSize = 1;
			simulation.setStepSize(stepSize);
			UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();
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
			assertTrue(simulation.sanityCheck());
			log.fatal("Running transversal");
			simulation.runTransversal();
			log.fatal("Transversal run complete");
			PopulationWriter.writeToXMLFile(simulation.getPopulation(),
					numberOfSteps, transSimOutput);
			PopulationWriter.writeToXMLFile(simulation.getPopulation(),
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
