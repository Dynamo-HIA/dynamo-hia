package nl.rivm.emi.cdm_v0.cohort;

import java.io.IOException;
import java.util.ArrayList;

import nl.rivm.emi.cdm_v0.exceptions.UnequalSizeException;
import nl.rivm.emi.cdm_v0.inputdata.cbs.MortalityCSVImporter;
import nl.rivm.emi.cdm_v0.outputdata.AgeOfDeathTallier;
import nl.rivm.emi.cdm_v0.state.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

//	public void runSimulationMK2(int numberOfIndividuals) {
//		try {
//			ArrayList<ArrayList> dataContainer = loadMortalityData();
//			// TimeBase ex machina.
//			TimeBase_0_105plus_step1 theTimeBase = TimeBase_0_105plus_step1
//					.getInstance();
//			AgeOfDeathTallier theTallier = new AgeOfDeathTallier(theTimeBase
//					.size());
//			SymptomRangesForAGender death = new SymptomRangesForAGender();
//			SymptomRange aliveProperties = new SymptomRange(0,
//					theTimeBase, null, dataContainer.get(1));
//			death.addSymptomRange(aliveProperties);
//			SymptomRange deadProperties = new SymptomRange(1, theTimeBase);
//			death.addSymptomRange(deadProperties);
//
//			Individual individual = new Individual(dataContainer.get(0),
//					dataContainer.get(1));
//			logger.error("Simulation start.");
//			for (int count = 0; count < numberOfIndividuals; count++) {
//				// TODO align datatypes.
//				theTallier.tallyOneDeath(individual.simulateAgeOfDeath()
//						.intValue());
//			}
//			logger.error("Simulation ready.");
//			theTallier.dumpSurvivalTable();
//		} catch (UnequalSizeException e) {
//			logger.error("Timeline and chances data are of unequal size.");
//		} catch (IOException e) {
//			logger.error("mortality data could not be loaded.");
//		} catch (RangePropertiesAtWrongIndexException e) {
//			logger.error(e.getClass().getName());
//		}
//	}

	private ArrayList<ArrayList> loadMortalityData() throws IOException {
		return MortalityCSVImporter
				.importFile2IntegerChances("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
	}

}
