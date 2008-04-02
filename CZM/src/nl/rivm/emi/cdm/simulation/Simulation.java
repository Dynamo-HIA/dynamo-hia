package nl.rivm.emi.cdm.simulation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.updaterules.AbstractUnboundOneToOneUpdateRule;
import nl.rivm.emi.cdm.updaterules.UpdateRuleMarker;
import nl.rivm.emi.cdm.updaterules.UpdateRuleStorage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Simulation extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Label of this Simulation.
	 */
	private String label = "Not initialized";

	/**
	 * Configured timeStep.
	 */
	private Float stepSize = 1F;

	/**
	 * Configured runMode.
	 */
	String runMode;

	/**
	 * The number of steps between population snapshots that will be written to
	 * disk.
	 */
	private int stepsBetweenSaves;

	/**
	 * The maximum number of steps this Simulation will run.
	 */
	private int stepsInRun;

	/**
	 * The stopping condition for the run.
	 */
	private String stoppingCondition;

	/**
	 * Population to use.
	 */
	private Population population = null;

	/**
	 * Configured updaterules.
	 */
	private UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();

	/**
	 * When the first Individual is processed, this transient HashMap is filled.
	 * After the run it is discarded.
	 */
	HashMap<Integer, UpdateRuleMarker> actualUpdateRules;

	/**
	 * Globally configured Characteristics.
	 */
	private CharacteristicsConfigurationMapSingleton characteristics;

	/**
	 * 
	 */
	public Simulation() {
		super();
	}

	/**
	 * Instantiate with label only, used in unit-tests.
	 * 
	 * @param label
	 * @param stepsInRun
	 *            TODO
	 */
	public Simulation(String label, int stepsInRun) {
		super();
		this.label = label;
		this.stepsInRun = stepsInRun;
	}

	/**
	 * Instantiate with an externally built Population.
	 * 
	 * @param label
	 * @param population
	 */
	public Simulation(String label, int stepsInRun, Population population) {
		super();
		this.stepsInRun = stepsInRun;
		this.population = population;
	}

	/**
	 * Build population from a XML configurationfile.
	 * 
	 * @param label
	 * @param populationFile
	 * @throws CZMConfigurationException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws CDMRunException
	 * @throws NumberFormatException
	 */
	public Simulation(String label, int stepsInRun, File populationFile)
			throws CDMConfigurationException, ParserConfigurationException,
			SAXException, IOException, NumberFormatException, CDMRunException {
		super();
		this.label = label;
		this.stepsInRun = stepsInRun;
		DOMBootStrap domBoot = new DOMBootStrap();
		population = domBoot.process2PopulationTree(populationFile, 1);
	}

	/**
	 * Method that does a sanity-check on the Simulation configuration. Only
	 * completeness is checked, consistency is not.
	 * 
	 * @return
	 */
	public boolean isConfigurationOK() {
		boolean checkOK = true;
		if (label == null) {
			checkOK = false;
		} else {
			if ("".equals(label)) {
				checkOK = false;
			}
		}
		if (stepSize <= 0) {
			checkOK = false;

		}
		if (runMode == null) {
			checkOK = false;
		} else {
			if ("".equals(runMode)) {
				checkOK = false;
			}
		}
		if (stepsBetweenSaves < 0) {
			checkOK = false;
		}
		if (stepsInRun <= 0) {
			checkOK = false;
		}
		if (stoppingCondition == null) {
			checkOK = false;
		} else {
			if ("".equals(stoppingCondition)) {
				checkOK = false;
			}
		}
		if (population == null) {
			checkOK = false;
		}
		if (updateRuleStorage == null) {
			checkOK = false;
		}
		if (characteristics == null) {
			checkOK = false;
		}
		return checkOK;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPopulationByFileName(String populationFileName) {
		// TODO load population.
		// this.population = population;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public int getStepsInRun() {
		return stepsInRun;
	}

	public void setStepsInRun(int stepsInRun) {
		this.stepsInRun = stepsInRun;
	}

	public float getStepSize() {
		return stepSize;
	}

	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}

	public String getStoppingCondition() {
		return stoppingCondition;
	}

	public void setStoppingCondition(String stoppingCondition) {
		this.stoppingCondition = stoppingCondition;
	}

	public UpdateRuleStorage getUpdateRuleStorage() {
		return updateRuleStorage;
	}

	public Population getPopulation() {
		return population;
	}

	public void run() throws CDMRunException {
		if (RunModes.LONGITUDINAL.equalsIgnoreCase(runMode)) {
			runLongitudinal();
		} else {
			if (RunModes.TRANSVERSAL.equalsIgnoreCase(runMode)) {
				runTransversal();
			} else {
				throw new CDMRunException("Illegal runmode: " + runMode);
			}
		}
	}

	private void runLongitudinal() throws CDMRunException {
		Iterator<Individual> individualIterator = population.iterator();
		while (individualIterator.hasNext()) {
			Individual individual = individualIterator.next();
			log.debug("Longitudinal: Processing individual "
					+ individual.getLabel());
			for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
				processCharVals(individual);
			}
		}
	}

	private void runTransversal() throws CDMRunException {
		for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
			Iterator<Individual> individualIterator = population.iterator();
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				log.debug("Transversal: Processing individual "
						+ individual.getLabel());
				processCharVals(individual);
			}
		}
	}

	private void processCharVals(Individual individual) throws CDMRunException {
		Iterator<IntCharacteristicValue> charValIterator = individual
				.iterator();
		while (charValIterator.hasNext()) {
			IntCharacteristicValue charVal = charValIterator.next();
			int charValIndex = charVal.getIndex();
			if (!characteristics.containsKey(charValIndex)) {
				log.warn("Individual " + individual.getLabel()
						+ " has a value at index " + charValIndex
						+ " for a non configured characteristic removing it.");
				charValIterator.remove();
				break;
			}
			Set<UpdateRuleMarker> rule = updateRuleStorage.getUpdateRules(
					charValIndex, stepSize);
			if (rule == null) {
				log.warn("Individual " + individual.getLabel()
						+ " has a characteristicValue at index " + charValIndex
						+ " without updaterule, removing it.");
				charValIterator.remove();
				break;
			}
			int oldValue = charVal.getCurrentValue();
			int newValue = rule.update(oldValue);
			charVal.appendValue(newValue);
			log.info("Updated charval at " + charVal.getIndex() + " from "
					+ oldValue + " to " + newValue + " for individual "
					+ individual.getLabel());
		}
	}

	public void setCharacteristics(
			CharacteristicsConfigurationMapSingleton characteristics) {
		characteristics = characteristics;
	}

	public void setTimeStep(float stepSize) {
		this.stepSize = stepSize;
	}

	public void setUpdateRuleStorage(UpdateRuleStorage updateRuleStorage) {
		this.updateRuleStorage = updateRuleStorage;
	}

	public int getStepsBetweenSaves() {
		return stepsBetweenSaves;
	}

	public void setStepsBetweenSaves(int stepsBetweenSaves) {
		this.stepsBetweenSaves = stepsBetweenSaves;
	}

}
