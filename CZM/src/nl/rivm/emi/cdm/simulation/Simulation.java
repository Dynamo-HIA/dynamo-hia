package nl.rivm.emi.cdm.simulation;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.updating.SimpleLoadableUpdateRules;
import nl.rivm.emi.cdm.updating.SimpleUpdateRules;
import nl.rivm.emi.cdm.updating.UpdateRulesBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Simulation extends DomLevelTraverser {

	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Label of this Simulation.
	 */
	String label = "Not initialized";

	Population population = null;

	// add Characteristics here.

	/**
	 * Prevent unintentional default construction.
	 */
	private Simulation() {
		super();
	}

	/**
	 * Instantiate with an externally built Population.
	 * 
	 * @param label
	 * @param population
	 */
	public Simulation(String label, Population population) {
		super();
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
	 */
	public Simulation(String label, File populationFile)
			throws CZMConfigurationException, ParserConfigurationException,
			SAXException, IOException {
		super();
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.parse(populationFile);
// Get the reading started
		Node rootNode = document.getFirstChild();
		population = new Population(rootNode);
		if (population != null) {
			// Initialize the rest.
		} else {
			log.error("Population could not be instantiated from "
					+ populationFile.getAbsolutePath());
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Population getPopulation() {
		return population;
	}

	public void run() {
		log.info("Starting simulation");
		if (population != null) {
			applyStaticIdentityUpdateRule();
			applyStaticPlusOneUpdateRule();
			applyLoadableSquareUpdateRule();
			log.info("Simulation complete");
		} else {
			log.info("Simulation not started, no population found.");

		}
	}

	private void applyStaticIdentityUpdateRule() {
		Individual individual;
		int count = 0;
		while ((individual = (Individual) population.nextIndividual()) != null) {
			count++;
			Integer currentValue = individual.getCharacteristicValue(1).getValue(); // TODO
			Integer newValue = SimpleUpdateRules.updateUnchanged(currentValue);
			log.info("Individual " + count + " updating " + currentValue
					+ " to " + newValue);
			individual.updateCharacteristicValue(1, new CharacteristicValue(newValue));
		}
	}

	private void applyStaticPlusOneUpdateRule() {
		Individual individual;
		int count = 0;
		while ((individual = (Individual) population.nextIndividual()) != null) {
			count++;
			Integer currentValue = individual.getCharacteristicValue(1).getValue(); // TODO
			Integer newValue = SimpleUpdateRules.updateAddOne(currentValue);
			log.info("Individual " + count + " updating " + currentValue
					+ " to " + newValue);
			individual.updateCharacteristicValue(1, new CharacteristicValue(newValue));
		}
	}

	private void applyLoadableSquareUpdateRule() {
		Individual individual;
		int count = 0;
		ClassLoader myLoader = ClassLoader.getSystemClassLoader();
		Class updateClass;
		try {
			updateClass = myLoader
					.loadClass("nl.rivm.emi.cdm.updating.SimpleLoadableUpdateRules");
			SimpleLoadableUpdateRules rules = (SimpleLoadableUpdateRules) updateClass
					.newInstance();

			while ((individual = (Individual) population.nextIndividual()) != null) {
				count++;
				Integer currentValue = individual
						.getCharacteristicValue(1).getValue(); // TODO Index
				Integer newValue = rules.updateOneToOneSquared(currentValue);
				log.info("Individual " + count + " updating loaded square "
						+ currentValue + " to " + newValue);
				individual.updateCharacteristicValue(1, new CharacteristicValue(newValue));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
