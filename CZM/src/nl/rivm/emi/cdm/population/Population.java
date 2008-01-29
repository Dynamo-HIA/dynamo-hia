package nl.rivm.emi.cdm.population;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

public class Population  extends TreeSet<Individual> {

	Log log = LogFactory.getLog(getClass().getName());
	String label = "Not initialized.";

	Iterator<Individual> iterator = null;

	public Population() {
		super();
	}

	public Population(Node node) throws CZMConfigurationException {
		boolean success = false;
		log.info("Node, name: " + node.getNodeName() + " value: "
				+ node.getNodeValue());
		Node childNode = node.getFirstChild();
		if (childNode != null) {
			int numberOfIndividuals = 0;
			while (childNode != null) {
				log.debug("Node, name: " + node.getNodeName() + " value: "
						+ node.getNodeValue());
//			if(!"#text".equals(childNode.getNodeName())){
				if (Individual.isMyNode(childNode)) {
					Individual indi = new Individual(childNode);
					add(indi);
					numberOfIndividuals++;
				} else {
					log.info("childNode is not an Individual, name: "
							+ childNode.getNodeName() + " value: "
							+ childNode.getNodeValue());

				}
//			}
				childNode = childNode.getNextSibling();
			}
			if(numberOfIndividuals == 0){
				throw new CZMConfigurationException("Population has no Individuals.");
			}
		} else {
			throw new CZMConfigurationException("Population has no contents.");
		}
	}


	private int checkNodeList(File populationFile, Node node)
			throws CZMConfigurationException {
		int numNodes = 1; // node.getLength();
		if (numNodes == 0) {
			throw new CZMConfigurationException(
					"No population found in configurationfile "
							+ populationFile.getAbsolutePath());
		}
		return numNodes;
	}

	public boolean addIndividual(Individual individual) {
		return add(individual);
	}

	/**
	 * Iterates over the Individuals in the Population. Restarts after the end
	 * has been reached (returns null once when it does).
	 * 
	 * @return null if there is no Individual (left). the next Individual. The
	 *         first call returns the first individual.
	 */
	public Individual nextIndividual() {
		Individual result = null;
		if (iterator == null) {
			iterator = super.iterator();
		}
		if (iterator.hasNext()) {
			result = iterator.next();
		} else {
			iterator = null;
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
