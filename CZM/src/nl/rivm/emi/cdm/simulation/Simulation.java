package nl.rivm.emi.cdm.simulation;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMap;
import nl.rivm.emi.cdm.characteristic.IntCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFactory;
import nl.rivm.emi.cdm.updating.SimpleLoadableUpdateRules;
import nl.rivm.emi.cdm.updating.SimpleUpdateRules;
import nl.rivm.emi.cdm.updating.UpdateRuleBaseClass;
import nl.rivm.emi.cdm.updating.UpdateRuleStorage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Simulation extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Label of this Simulation.
	 */
	private String label = "Not initialized";

	/**
	 * The maximum number of steps this Simulation will run.
	 */
	private int numberOfSteps;

	/**
	 * Configured Characteristics.
	 */
	private CharacteristicsConfigurationMap Characteristics;

	/**
	 * Population to use.
	 */
	private Population population = null;

	/**
	 * Configured stepsize.
	 */
	private int stepSize = 1;

	/**
	 * Configured updaterules.
	 */
	private UpdateRuleStorage updateRuleStorage = new UpdateRuleStorage();

	/**
	 * Prevent unintentional default construction.
	 */
	private Simulation() {
		super();
	}

	/**
	 * Instantiate with label only, used in unit-tests.
	 * 
	 * @param label
	 * @param numberOfSteps
	 *            TODO
	 */
	public Simulation(String label, int numberOfSteps) {
		super();
		this.label = label;
		this.numberOfSteps = numberOfSteps;
	}

	/**
	 * Instantiate with an externally built Population.
	 * 
	 * @param label
	 * @param population
	 */
	public Simulation(String label, int numberOfSteps, Population population) {
		super();
		this.numberOfSteps = numberOfSteps;
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
	 * @throws CZMRunException
	 * @throws NumberFormatException
	 */
	public Simulation(String label, int numberOfSteps, File populationFile)
			throws CZMConfigurationException, ParserConfigurationException,
			SAXException, IOException, NumberFormatException, CZMRunException {
		super();
		this.label = label;
		this.numberOfSteps = numberOfSteps;
		makeAndSetPopulation(populationFile);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}

	public void makeAndSetPopulation(File populationFile)
			throws ParserConfigurationException, SAXException, IOException,
			CZMConfigurationException, NumberFormatException, CZMRunException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.parse(populationFile);
		// Get the reading started
		Node rootNode = document.getFirstChild();
		PopulationFactory populationFactory = new PopulationFactory("pop");
		boolean success = populationFactory.makeIt(rootNode, this);
		if (!success) {
			log.error("Population construction produced errors.");
		}
	}

	public Population getPopulation() {
		return population;
	}

//	public void run() throws CZMRunException {
//		log.info("Starting simulation");
//		if (population != null) {
//			// applyStaticIdentityUpdateRule();
//			// applyStaticPlusOneUpdateRule();
//			// applyLoadableSquareUpdateRule();
//			log.info("Simulation complete");
//		} else {
//			log.info("Simulation not started, no population found.");
//
//		}
//	}

	public void runLongitudinal() throws CZMRunException {
		Iterator<Individual> individualIterator = population.iterator();
		while (individualIterator.hasNext()) {
			Individual individual = individualIterator.next();
			log.debug("Longitudinal: Processing individual " + individual.getLabel());
			Iterator<IntCharacteristicValue> charValIterator = individual
					.iterator();
			for (int stepCount = 0; stepCount < numberOfSteps; stepCount++) {
			while (charValIterator.hasNext()) {
				IntCharacteristicValue charVal = charValIterator.next();
				int charValIndex = charVal.getIndex();
				if (!Characteristics.containsKey(charValIndex)) {
					log
							.warn("Individual "
									+ individual.getLabel()
									+ " has a value at index "
									+ charValIndex
									+ " for a non configured characteristic removing it.");
					charValIterator.remove();
					break;
				}
				UpdateRuleBaseClass rule = updateRuleStorage.getUpdateRule(
						charValIndex, stepSize);
				if (rule == null) {
					log.warn("Individual " + individual.getLabel()
							+ " has a characteristicValue at index "
							+ charValIndex
							+ " without updaterule, removing it.");
					charValIterator.remove();
					break;
				}
					int oldValue = charVal.getCurrentValue();
					int newValue = rule.updateSelf(oldValue);
					charVal.appendValue(newValue);
					log.info("Updated charval at " + charVal.getIndex()
							+ " from " + oldValue + " to " + newValue
							+ " for individual " + individual.getLabel());
				}
			}
		}
	}

	public void runTransversal() throws CZMRunException {
		for (int stepCount = 0; stepCount < numberOfSteps; stepCount++) {
			Iterator<Individual> individualIterator = population.iterator();
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				Iterator<IntCharacteristicValue> charValIterator = individual
						.iterator();
				log.debug("Transversal: Processing individual " + individual.getLabel());
				while (charValIterator.hasNext()) {
					IntCharacteristicValue charVal = charValIterator.next();
					int charValIndex = charVal.getIndex();
					if (!Characteristics.containsKey(charValIndex)) {
						log
								.warn("Individual "
										+ individual.getLabel()
										+ " has a value at index "
										+ charValIndex
										+ " for a non configured characteristic removing it.");
						charValIterator.remove();
						break;
					}
					UpdateRuleBaseClass rule = updateRuleStorage.getUpdateRule(
							charValIndex, stepSize);
					if (rule == null) {
						log.warn("Individual " + individual.getLabel()
								+ " has a characteristicValue at index "
								+ charValIndex
								+ " without updaterule, removing it.");
						charValIterator.remove();
						break;
					}
					int oldValue = charVal.getCurrentValue();
					int newValue = rule.updateSelf(oldValue);
					charVal.appendValue(newValue);
					log.info("Updated charval at " + charVal.getIndex()
							+ " from " + oldValue + " to " + newValue
							+ " for individual " + individual.getLabel());
				}
			}
		}
	}

	// private void applyStaticIdentityUpdateRule() throws CZMRunException {
	// Individual individual;
	// int count = 0;
	// while ((individual = (Individual) population.nextIndividual()) != null) {
	// count++;
	// int currentValue = individual.getCurrentCharacteristicValue(1)
	// .getCurrentValue();
	// int newValue = SimpleUpdateRules.updateUnchanged(currentValue);
	// log.info("Individual " + count + " updating " + currentValue
	// + " to " + newValue);
	// individual.getCurrentCharacteristicValue(1).appendValue(newValue);
	// }
	// }
	//
	// private void applyStaticPlusOneUpdateRule() throws CZMRunException {
	// Individual individual;
	// int count = 0;
	// while ((individual = (Individual) population.nextIndividual()) != null) {
	// count++;
	// IntCharacteristicValue charVal = individual
	// .getCurrentCharacteristicValue(1);
	// int currentValue = charVal.getCurrentValue();
	// int newValue = SimpleUpdateRules.updateAddOne(currentValue);
	// log.info("Individual " + count + " updating " + currentValue
	// + " to " + newValue);
	// charVal.appendValue(newValue);
	// }
	// }
	//
	// private void applyLoadableSquareUpdateRule() throws CZMRunException {
	// Individual individual;
	// int count = 0;
	// ClassLoader myLoader = ClassLoader.getSystemClassLoader();
	// Class updateClass;
	// try {
	// updateClass = myLoader
	// .loadClass("nl.rivm.emi.cdm.updating.SimpleLoadableUpdateRules");
	// SimpleLoadableUpdateRules rules = (SimpleLoadableUpdateRules) updateClass
	// .newInstance();
	//
	// while ((individual = (Individual) population.nextIndividual()) != null) {
	// count++;
	// IntCharacteristicValue charVal = individual
	// .getCurrentCharacteristicValue(1);
	// int currentValue = charVal.getCurrentValue();
	// int newValue = rules.updateOneToOneSquared(currentValue);
	// log.info("Individual " + count + " updating loaded square "
	// + currentValue + " to " + newValue);
	// charVal.appendValue(newValue);
	// }
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InstantiationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void setCharacteristics(
			CharacteristicsConfigurationMap characteristics) {
		Characteristics = characteristics;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

	public void setUpdateRuleStorage(UpdateRuleStorage updateRuleStorage) {
		this.updateRuleStorage = updateRuleStorage;
	}

	public boolean sanityCheck() {
		boolean allOK = true;
		if (myElementName == null || "".equals(myElementName)) {
			allOK = false;
		}
		if (!(numberOfSteps > 0)) {
			allOK = false;
		}
		if (Characteristics == null) {
			allOK = false;
		}
		if (population == null) {
			allOK = false;
		}
		if (!(stepSize > 0)) {
			allOK = false;
		}
		if (updateRuleStorage == null) {
			allOK = false;
		}
		return allOK;
	}
}
