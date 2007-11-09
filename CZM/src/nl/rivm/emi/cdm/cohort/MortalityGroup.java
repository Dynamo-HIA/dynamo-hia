package nl.rivm.emi.cdm.cohort;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.exceptions.UnequalSizeException;
import nl.rivm.emi.cdm.inputdata.cbs.MortalityCSVImporter;
import nl.rivm.emi.cdm.outputdata.AgeOfDeathTallier;
import nl.rivm.emi.cdm.state.Individual;

public class MortalityGroup {
	Log logger = LogFactory.getLog(getClass().getName());

	public void runSimulation(int numberOfIndividuals) {
		try {
			ArrayList<ArrayList> dataContainer = loadMortalityData();
			AgeOfDeathTallier theTallier = new AgeOfDeathTallier(dataContainer
					.get(0).size());
			Individual individual = new Individual(dataContainer.get(0),
					dataContainer.get(1));
			logger.error("Simulation start.");
			for (int count = 0; count < numberOfIndividuals; count++) {
				// TODO align datatypes.
				theTallier.tallyOneDeath(individual.simulateAgeOfDeath()
						.intValue());
			}
			logger.error("Simulation ready.");
			theTallier.dumpSurvivalTable();
		} catch (UnequalSizeException e) {
			logger.error("Timeline and chances data are of unequal size.");
		} catch (IOException e) {
			logger.error("mortality data could not be loaded.");
		}
	}

	private ArrayList<ArrayList> loadMortalityData() throws IOException {
		return MortalityCSVImporter
				.importFile2IntegerChances("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
	}

}
