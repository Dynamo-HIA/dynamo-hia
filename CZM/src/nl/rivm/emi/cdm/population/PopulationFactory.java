package nl.rivm.emi.cdm.population;

import java.io.File;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.IndividualFactory;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

public class PopulationFactory extends XMLConfiguredObjectFactory {

	Log log = LogFactory.getLog(getClass().getName());

	public PopulationFactory(String elementName) {
		super(elementName);
	}

	/**
	 * Try to build a Population taking the passed Node in a DOM-tree as a
	 * startingpoint. The level of this Node is scanned for the required
	 * elementName.
	 * 
	 * @param node
	 * @throws CZMConfigurationException
	 */
	public Population makeIt(Node node) throws CZMConfigurationException {
		boolean success = false;
		log.info("Passed Node, name: " + node.getNodeName() + " value: "
				+ node.getNodeValue());
		Population createdPopulation = null;
		Node myNode = findMyNodeAtThisLevel(node);
		if (myNode != null) {
			createdPopulation = new Population();
			// TODO Add configuration for the Population later.
			IndividualFactory individualFactory = null; // @@@@@@@@
			Node childNode = node.getFirstChild();
			if (childNode != null) {
				int numberOfIndividuals = 0;
				while (childNode != null) {
					log.debug("Node, name: " + node.getNodeName() + " value: "
							+ node.getNodeValue());
					// if(!"#text".equals(childNode.getNodeName())){
					if (Individual.isMyNode(childNode)) {
						Individual indi = new Individual(childNode);
						numberOfIndividuals++;
					} else {
						log.info("childNode is not an Individual, name: "
								+ childNode.getNodeName() + " value: "
								+ childNode.getNodeValue());

					}
					// }
					childNode = childNode.getNextSibling();
				}
				if (numberOfIndividuals == 0) {
					throw new CZMConfigurationException(
							"Population has no Individuals.");
				}
			} else {
				throw new CZMConfigurationException(
						"Population has no contents.");
			}
		}
		return createdPopulation;
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
}
