package nl.rivm.emi.cdm.updating.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFactory;
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

public class TestUpdateRuleContainers {
	Log log = LogFactory.getLog(getClass().getName());

	static public class UpdateRuleOneOne extends UpdateRuleBaseClass {
		public UpdateRuleOneOne() {
			super(1, 1);
		}

		@Override
		public int updateSelf(int currentValue) {
			// TODO Auto-generated method stub
			return 0;
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
	public void storeByCharacteristicId() {
		UpdateRulesByCharIdContainer updateRulesByCharIdContainer = new UpdateRulesByCharIdContainer();
		assertNotNull(updateRulesByCharIdContainer);
		UpdateRuleBaseClass updateRule = updateRulesByCharIdContainer.getUpdateRule(1);
		assertNull(updateRule);
		UpdateRuleBaseClass updateRuleOneOne = new UpdateRuleOneOne();
		assertNotNull(updateRuleOneOne);
		updateRulesByCharIdContainer.putUpdateRule(updateRuleOneOne);
		UpdateRuleBaseClass storedUpdateRule = updateRulesByCharIdContainer.getUpdateRule(1);
		assertNotNull(storedUpdateRule);
	}

	@Test
	public void store() {
		UpdateRuleStorage storage = new UpdateRuleStorage();
		assertNotNull(storage);
		UpdateRuleBaseClass updateRuleOneOne = new UpdateRuleOneOne();
		assertNotNull(updateRuleOneOne);
		storage.addUpdateRule(updateRuleOneOne);
		UpdateRuleBaseClass updateRuleTwoTwo = new UpdateRuleTwoTwo();
		assertNotNull(updateRuleTwoTwo);
		storage.addUpdateRule(updateRuleTwoTwo);
		UpdateRuleBaseClass updateRuleSixFour = new UpdateRuleSixFour();
		assertNotNull(updateRuleSixFour);
		storage.addUpdateRule(updateRuleSixFour);
		UpdateRuleBaseClass updateRule = storage.getUpdateRule(0,0);
		assertNull(updateRule);
		updateRule = storage.getUpdateRule(1,1);
		assertNotNull(updateRule);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.updating.test.TestUpdateRuleContainers.class);
	}

}
