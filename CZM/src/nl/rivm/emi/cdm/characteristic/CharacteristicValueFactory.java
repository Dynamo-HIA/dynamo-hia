package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.characteristic.CharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
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
public class CharacteristicValueFactory extends XMLConfiguredObjectFactory {
	Log log = LogFactory.getLog(getClass().getName());

	public CharacteristicValueFactory(String elementName) {
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
	public boolean makeIt(Node node, Individual individual)
			throws CZMConfigurationException {
		boolean success = false;
		log.info("Passed Node, name: " + node.getNodeName() + " value: "
				+ node.getNodeValue());
		// Single CharacteristicValue for now. TODO Extend.
		Node myNode = findMyNodeAtThisLevel(node);
		if (myNode != null) {
			CharacteristicValue characteristicValue = new CharacteristicValue(
					null);
			Node childNode = node.getFirstChild();
			if (childNode != null) {
				while (childNode != null) {
					String childNodeValue = (String) childNode.getNodeValue();
					log.debug("Node, name: " + childNode.getNodeName()
							+ " value: " + childNodeValue);
					characteristicValue.setValue(childNodeValue);
					if ((childNodeValue != null)
							&& (childNodeValue.equals(characteristicValue
									.getValue().toString()))) {
						success = true;
					} else {
						log
								.warn("Nodevalue: "
										+ childNodeValue
										+ " did not fit CharacteristicValue and became: "
										+ characteristicValue.getValue()
												.toString());
					}
					individual.luxeSet(0, characteristicValue);
					childNode = childNode.getNextSibling();
				}
			}
		} else {
			throw new CZMConfigurationException("No CharacteristicValue found.");
		}
		return success;
	}
}
