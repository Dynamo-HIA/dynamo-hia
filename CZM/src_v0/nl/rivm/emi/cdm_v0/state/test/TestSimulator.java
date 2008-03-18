package nl.rivm.emi.cdm_v0.state.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm_v0.exceptions.UnequalSizeException;
import nl.rivm.emi.cdm_v0.inputdata.cbs.MortalityCSVImporter;
import nl.rivm.emi.cdm_v0.state.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class TestSimulator {
	Log logger = LogFactory.getLog(getClass().getName());

	@Test
	public void initializeIndividualWithWrongData() {
		ArrayList<Float> timeLine = new ArrayList<Float>();
		timeLine.add(new Float(0)); // length = 1
		ArrayList<Integer> chances = new ArrayList<Integer>(); // length = 0
		try {
			Individual individual = new Individual(timeLine, chances);
		} catch (UnequalSizeException e) {
			assertNotNull(e);
			logger.info(e.getClass().getName() + " caught.");
			logger.info("Message: " + e.getMessage());
		}
	}

	@Test
	public void testWithPresentMortalityFile() {
		try {
			ArrayList<ArrayList> dataContainer = MortalityCSVImporter
					.importFile2IntegerChances("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
			assertNotNull(dataContainer);
			try {
				Individual individual = new Individual(dataContainer.get(0),
						dataContainer.get(1));
				logger.info("Mortality simulation run. Age of death: "
						+ individual.simulateAgeOfDeath());
			} catch (UnequalSizeException e) {
				assertNotNull(e);
				logger.info(e.getClass().getName() + " caught.");
				logger.info("Message: " + e.getMessage());
			}
		} catch (IOException e) {
			assertFalse(true); // Should not come here.
		}

	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm_v0.state.test.TestSimulator.class);
	}
}
