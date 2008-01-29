package nl.rivm.emi.cdm.individual;

import java.util.ArrayList;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.characteristic.CharacteristicValue;
import nl.rivm.emi.cdm.characteristic.CharacteristicValueFactory;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * Simplest individual that can be used in a simulation.
 * 
 * @author mondeelr
 * 
 */
public class IndividualFactory extends XMLConfiguredObjectFactory {
	Log log = LogFactory.getLog(getClass().getName());

	public IndividualFactory(String elementName) {
		super(elementName);
	}

	/**
	 * Try to build Individuals taking the passed Node in a DOM-tree as a
	 * startingpoint. The level of this Node is scanned for the required
	 * elementName.
	 * 
	 * @param node
	 * @param Population
	 *            to put Individuals into.
	 * @throws CZMConfigurationException
	 */
	public boolean makeIt(Node node, Population population)
			throws CZMConfigurationException {
		boolean success = false;
		log.info("Passed Node, name: " + node.getNodeName() + " value: "
				+ node.getNodeValue());
		Node myNode = findMyNodeAtThisLevel(node);
		while (myNode != null) {
			Individual currentIndividual = new Individual("New individual");
			Node childNode = node.getFirstChild();
			CharacteristicValueFactory charValFactory = new CharacteristicValueFactory(
					"charval");
			success = charValFactory.makeIt(childNode, currentIndividual);
			if (!success) {
				log.warn("CharacteristicValue factoring went awry");
			}
		}
		return success;
	}
}
