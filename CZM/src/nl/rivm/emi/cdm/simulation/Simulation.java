package nl.rivm.emi.cdm.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.rules.update.base.ManyToManyUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRules4Simulation;
import nl.rivm.emi.cdm.rules.update.dynamo.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.stax.StAXEntryPoint;

import org.apache.commons.configuration.ConfigurationException;
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
	private UpdateRules4Simulation updateRuleStorage = new UpdateRules4Simulation();

	// /**
	// * When the first Individual is processed, this transient HashMap is
	// filled.
	// * After the run it is discarded.
	// */
	// UpdateRules4Simulation actualUpdateRules;

	/**
	 * Globally configured Characteristics.
	 */
	private CharacteristicsConfigurationMapSingleton characteristics = CharacteristicsConfigurationMapSingleton
			.getInstance();

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

	public void setPopulationByFileName_DOM(String populationFileName)
			throws ConfigurationException {
		File populationFile = new File(populationFileName);
		if (populationFile.exists() && populationFile.isFile()
				&& populationFile.canRead()) {
			DOMBootStrap domBoot = new DOMBootStrap();
			try {
				population = domBoot.process2PopulationTree(populationFile,
						this.stepsInRun);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ConfigurationException(
						"Error reading populationfile " + populationFileName
								+ ", Exception thrown: "
								+ e.getClass().getName() + " message "
								+ e.getMessage());
			}
		} else {
			throw new ConfigurationException("Populationfile "
					+ populationFileName + ", does not exist or is no file.");

		}
	}

	public void setPopulationByFileName_StAX(String populationFileName)
			throws ConfigurationException {
		File populationFile = new File(populationFileName);
		if (populationFile.exists() && populationFile.isFile()
				&& populationFile.canRead()) {
			try {
				population = (Population) StAXEntryPoint
						.processFile(populationFile);
			} catch (FileNotFoundException e) {
				throw new ConfigurationException(
						"Error reading populationfile " + populationFileName
								+ ", Exception thrown: "
								+ e.getClass().getName() + " message "
								+ e.getMessage());
			} catch (XMLStreamException e) {
				throw new ConfigurationException(
						"Error reading populationfile " + populationFileName
								+ ", Exception thrown: "
								+ e.getClass().getName() + " message "
								+ e.getMessage());
			} catch (UnexpectedFileStructureException e) {
				throw new ConfigurationException(
						"Error reading populationfile " + populationFileName
								+ ", Exception thrown: "
								+ e.getClass().getName() + " message "
								+ e.getMessage());
			}
		} else {
			throw new ConfigurationException("Populationfile "
					+ populationFileName + ", does not exist or is no file.");

		}
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

	public UpdateRules4Simulation getUpdateRuleStorage() {
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
		int count = 0;
		while (individualIterator.hasNext()) {
			Individual individual = individualIterator.next();
			if(count % 100 == 0){
			log.warn("Longitudinal: Processing individual # "
					+ count /* individual.getLabel()*/ + " at " + System.currentTimeMillis());
			}
			count++;
			for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
				processCharVals(individual);
			}
		}
		log.warn("Longitudinal: Done processing "+ count + " individuals at " + System.currentTimeMillis());

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
		Iterator<CharacteristicValueBase> charValIterator = individual
				.iterator();
		while (charValIterator.hasNext()) {
			CharacteristicValueBase charValBase = charValIterator.next();
			if (charValBase instanceof IntCharacteristicValue) {
				IntCharacteristicValue charVal = (IntCharacteristicValue) charValBase;
				if (!handleIntCharVal(charVal, individual)) {
					charValIterator.remove();
				}
			} else {
				if (charValBase instanceof FloatCharacteristicValue) {
					FloatCharacteristicValue charVal = (FloatCharacteristicValue) charValBase;
					if (!handleFloatCharVal(charVal, individual)) {
						charValIterator.remove();
					}
				}
				else
				{
					if (charValBase instanceof CompoundCharacteristicValue) {
						CompoundCharacteristicValue charVal = (CompoundCharacteristicValue) charValBase;
						if (!handleCompoundCharVal(charVal, individual)) {
							charValIterator.remove();
						}
					}
				}
			}
		}
	}

	private boolean handleIntCharVal(IntCharacteristicValue intCharVal,
			Individual individual) throws CDMRunException {
		log.debug("Entering handleIntCharVal");
		boolean keep = false;
		int charValIndex = intCharVal.getIndex();
		UpdateRuleMarker updateRule = updateRuleStorage
				.getUpdateRule(charValIndex);
		try {
			if (!characteristics.containsKey(charValIndex)) {
				log.warn("Individual " + individual.getLabel()
						+ " has a value at index " + charValIndex
						+ " for a non configured characteristic removing it.");
			} else {
				if (updateRule == null) {
					log.warn("Individual " + individual.getLabel()
							+ " has a characteristicValue at index "
							+ charValIndex
							+ " without updaterules, removing it.");
				} else {
					if (updateRule instanceof OneToOneUpdateRuleBase) {

						int oldValue = intCharVal.getCurrentValue();
						Integer newValue = (Integer) ((OneToOneUpdateRuleBase) updateRule)
								.update(new Integer(oldValue));
						intCharVal.appendValue(newValue);
						log.info("Updated charval at " + intCharVal.getIndex()
								+ " for " + individual.getLabel() + " from "
								+ oldValue + " to " + newValue);
						keep = true;
					} else {
						if (updateRule instanceof ManyToOneUpdateRuleBase) {
							Object[] charVals = new Object[individual.size()];
							for (int count = 0; count < charVals.length; count++) {
								CharacteristicValueBase charValBase = individual
										.get(count);
								if (charValBase != null) {
									Object currValue = charValBase
											.getCurrentValue();
									charVals[count] = currValue;
								} else {
									charVals[count] = null;
								}
							}
							float oldValue = intCharVal.getCurrentValue();
							// int index = intCharVal.getIndex();
							Integer newValue = (Integer) ((ManyToOneUpdateRuleBase) updateRule)
									.update(charVals);
							if (newValue != null) {
								intCharVal.appendValue(newValue);
								log.info("Updated charval at "
										+ intCharVal.getIndex() + " for "
										+ individual.getLabel() + " from "
										+ oldValue + " to " + newValue);
								keep = true;
							} else {
								throw new CDMRunException(
										"ManyToOne update rule produced a null result, aborting.");
							}
						} else
							throw new CDMRunException(
									"Update rule not in updateRuleBase");
					}
				}
			}

			return keep;
		} catch (CDMUpdateRuleException e) {
			log.warn("Individual " + individual.getLabel()
					+ " has a characteristicValue at index " + charValIndex
					+ " with updaterule mismatch: "
					+ updateRule.getClass().getName() + ", removing it.");
			return keep;
		}
	}

	private boolean handleFloatCharVal(FloatCharacteristicValue floatCharVal,
			Individual individual) throws CDMRunException {
		log.debug("Entering handleFloatCharVal");
		boolean keep = false;
		int charValIndex = floatCharVal.getIndex();
		UpdateRuleMarker updateRule = updateRuleStorage
				.getUpdateRule(charValIndex);
		try {
			if (!characteristics.containsKey(charValIndex)) {
				log.warn("Individual " + individual.getLabel()
						+ " has a value at index " + charValIndex
						+ " for a non configured characteristic removing it.");
			} else {
				if (updateRule == null) {
					log.warn("Individual " + individual.getLabel()
							+ " has a characteristicValue at index "
							+ charValIndex
							+ " without an updaterule, removing it.");
				} else {
					if (updateRule instanceof OneToOneUpdateRuleBase) {
						float oldValue = floatCharVal.getCurrentValue();
						Float newValue = (Float) ((OneToOneUpdateRuleBase) updateRule)
								.update(new Float(oldValue));
						floatCharVal.appendValue(newValue);
						log.info("Updated charval at "
								+ floatCharVal.getIndex() + " for "
								+ individual.getLabel() + " from " + oldValue
								+ " to " + newValue);
						keep = true;
					} else {
						if (updateRule instanceof ManyToOneUpdateRuleBase) {
							Object[] charVals = new Object[individual.size()];
							for (int count = 0; count < charVals.length; count++) {
								CharacteristicValueBase charValBase = individual
										.get(count);
								if (charValBase != null) {
									Object currValue = charValBase
											.getCurrentValue();
									charVals[count] = currValue;
								} else {
									charVals[count] = null;
								}
							}
							float oldValue = floatCharVal.getCurrentValue();
							// int index = floatCharVal.getIndex();
							Float newValue = (Float) ((ManyToOneUpdateRuleBase) updateRule)
									.update(charVals);
							if (newValue != null) {
								floatCharVal.appendValue(newValue);
								log.info("Updated charval at "
										+ floatCharVal.getIndex() + " for "
										+ individual.getLabel() + " from "
										+ oldValue + " to " + newValue);
								keep = true;
							} else {
								throw new CDMRunException(
										"ManyToOne update rule produced a null result, aborting.");
							}
						} else
							throw new CDMRunException(
									"Update rule not in updateRuleBase");
					}
				}
			}

			return keep;
		} catch (CDMUpdateRuleException e) {
			e.printStackTrace(); // TODO remove
			log.warn("Individual " + individual.getLabel()
					+ " has a characteristicValue at index " + charValIndex
					+ " with updaterule mismatch: "
					+ updateRule.getClass().getName() + ", removing it.");
			return keep;
		}
	}

	private boolean handleCompoundCharVal(
			CompoundCharacteristicValue diseaseCharVal, Individual individual)
			throws CDMRunException {
		log.debug("Entering handleCompoundCharVal");
		boolean keep = false;
		int charValIndex = diseaseCharVal.getIndex();
		UpdateRuleMarker updateRule = updateRuleStorage
				.getUpdateRule(charValIndex);
		try {
			if (!characteristics.containsKey(charValIndex)) {
				log.warn("Individual " + individual.getLabel()
						+ " has a value at index " + charValIndex
						+ " for a non configured characteristic removing it.");
			} else {
				if (updateRule == null) {
					log.warn("Individual " + individual.getLabel()
							+ " has a characteristicValue at index "
							+ charValIndex
							+ " without an updaterule, removing it.");
				} else {
					/**
					 * for type diseaseCharVal only ManyToManyUpdateRules are
					 * allowed
					 */

					/*
					 * if (updateRule instanceof OneToOneUpdateRuleBase) {
					 * float[] oldValue = diseaseCharVal.getCurrentValue();
					 * Float[] newValue = (Float) ((OneToOneUpdateRuleBase)
					 * updateRule) .update(new Float(oldValue));
					 * diseaseCharVal.appendValue(newValue);
					 * log.info("Updated charval at " + floatCharVal.getIndex()
					 * + " for " + individual.getLabel() + " from " + oldValue +
					 * " to " + newValue); keep = true; } else {
					 */
					if (updateRule instanceof ManyToManyUpdateRuleBase) {
						log.debug("Entering ManyToManyUpdateRule" + System.currentTimeMillis());
						Object[] charVals = new Object[individual.size()];
						for (int count = 0; count < charVals.length; count++) {
							CharacteristicValueBase charValBase = individual
									.get(count);
							if (charValBase != null) {
								Object currValue = charValBase
										.getCurrentValue();
								charVals[count] = currValue;
							} else {
								charVals[count] = null;
							}
						}
						float[] oldValue = diseaseCharVal
								.getCurrentWrapperlessValue();
						// int index = floatCharVal.getIndex();
						float[] newValue = (float[]) ((ManyToManyUpdateRuleBase) updateRule)
								.update(charVals);
						if (newValue != null) {
							diseaseCharVal.appendValue(newValue);
							log.info("Updated charval at "
									+ diseaseCharVal.getIndex() + " for "
									+ individual.getLabel() + " from "
									+ oldValue + " to " + newValue);
							keep = true;
						} else {
							throw new CDMRunException(
									"ManyToMany update rule produced a null result, aborting.");
						}
						log.debug("Exiting ManyToManyUpdateRule " + System.currentTimeMillis());
					} else
						throw new CDMRunException(
								"Update rule not in updateRuleBase or not defined for this type of"
										+ "characteristic");
				}
			}

			return keep;
		} catch (CDMUpdateRuleException e) {
			e.printStackTrace(); // TODO remove
			log.warn("Individual " + individual.getLabel()
					+ " has a characteristicValue at index " + charValIndex
					+ " with updaterule mismatch: "
					+ updateRule.getClass().getName() + ", removing it.");
			return keep;
		}
	}

	public void setCharacteristics(
			CharacteristicsConfigurationMapSingleton characteristics) {
		characteristics = characteristics;
	}

	public void setTimeStep(float stepSize) {
		this.stepSize = stepSize;
	}

	public void setUpdateRuleStorage(UpdateRules4Simulation updateRuleStorage) {
		this.updateRuleStorage = updateRuleStorage;
	}

	public int getStepsBetweenSaves() {
		return stepsBetweenSaves;
	}

	public void setStepsBetweenSaves(int stepsBetweenSaves) {
		this.stepsBetweenSaves = stepsBetweenSaves;
	}

}
