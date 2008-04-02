package nl.rivm.emi.cdm.characteristic.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Simplest individual that can be used in a simulation.
 * 
 * @author mondeelr
 * 
 */
public class CharacteristicValueFactory extends XMLConfiguredObjectFactory {
	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Generic patterns to prevent NumberFormatExceptions.
	 */
	Pattern posIntPattern = Pattern.compile("[0-9]+");

	Pattern floatPattern = Pattern.compile("[0-9]+[\\.]?[0-9]*");

	/**
	 * Specialised pattern to detect 0.
	 */
	Pattern zeroPattern = Pattern.compile("0+");

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
	 * @throws CDMRunException 
	 * @throws NumberFormatException 
	 */
	public boolean makeIt(Node node, Individual individual, int numberOfSteps)
			throws CDMConfigurationException, NumberFormatException, CDMRunException {
		boolean anyErrors = false;
		if (node != null) {
			log.info("Passed Node, name: " + node.getNodeName() + " value: "
					+ node.getNodeValue());
			// Single CharacteristicValue for now. TODO Extend.
			Node myNode = findMyNodeAtThisLevel(node);
			int numNodesFound = 0;
			while (myNode != null) {
				numNodesFound++;
				if (!processMyNode(individual, myNode, numberOfSteps)) {
					anyErrors = true;
				}
				myNode = findMyNextNodeAtThisLevel(myNode);
			}
			if (numNodesFound == 0) {
				throw new CDMConfigurationException(
						"No CharacteristicValue found.");
			}
		}
		return !anyErrors;
	}

	private boolean processMyNode(Individual individual, Node myNode,
			int numberOfSteps) throws CDMConfigurationException, NumberFormatException, CDMRunException {
		boolean success = false;
		NamedNodeMap myAttributes = myNode.getAttributes();
		if (myAttributes != null) {
			Node indexNode = myAttributes.getNamedItem("id");
			if (indexNode == null) {
				indexNode = myAttributes.getNamedItem("index");
				if (indexNode == null) {
					throw new CDMConfigurationException(
							"CharacteristicValue without index attribute found.");
				}
			}
			String index = indexNode.getNodeValue();
			Matcher matcher = posIntPattern.matcher(index);
			boolean indexPosInt = matcher.matches();
			matcher = zeroPattern.matcher(index);
			boolean indexZero = matcher.matches();
			boolean indexOK = indexPosInt && !indexZero;
			Node valueNode = myAttributes.getNamedItem("vl");
			if (valueNode == null) {
				valueNode = myAttributes.getNamedItem("value");
				if (valueNode == null) {
					throw new CDMConfigurationException(
							"CharacteristicValue without value attribute found.");
				}
			}
			String value = valueNode.getNodeValue();
			matcher = posIntPattern.matcher(value);
			boolean valueOK = matcher.matches();
			if (indexOK && valueOK) {
				IntCharacteristicValue intCharacteristicValue = new IntCharacteristicValue(
						numberOfSteps, Integer.parseInt(index));
				intCharacteristicValue.appendValue(Integer.parseInt(value));
				log.debug("Setting CharacteristicValue "
						+ intCharacteristicValue.getValue()
						+ " in Individual at index "
						+ +intCharacteristicValue.getIndex());
				individual.luxeSet(Integer.parseInt(index),
						intCharacteristicValue);
				success = true;
			} else {
				log.warn("CharacteristicValue attribute(s) no integer.");
			}
		} else {
			throw new CDMConfigurationException(
					"CharacteristicValue without attributes found.");
		}
		return success;
	}
}
