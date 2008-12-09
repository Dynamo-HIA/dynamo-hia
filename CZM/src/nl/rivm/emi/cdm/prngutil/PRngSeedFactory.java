package nl.rivm.emi.cdm.prngutil;

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
public class PRngSeedFactory extends XMLConfiguredObjectFactory {
	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Generic patterns to prevent NumberFormatExceptions.
	 */
	Pattern posIntPattern = Pattern.compile("[0-9]+");
	
/* changed by hendriek to allow for negative numbers 
 * old version was
 * Pattern longPattern = Pattern.compile("[0-9]+[\\.]?[0-9]*");

 */
	Pattern longPattern = Pattern.compile("^-?[0-9]+[\\.]?[0-9]*");

	/**
	 * Specialised pattern to detect 0.
	 */
	Pattern zeroPattern = Pattern.compile("0+");

	public PRngSeedFactory(String elementName) {
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
	public boolean makeIt(Node node, Individual individual)
			throws CDMConfigurationException, NumberFormatException,
			CDMRunException {
		boolean anyErrors = false;
		if (node != null) {
			log.info("Passed Node, name: " + node.getNodeName() + " value: "
					+ node.getNodeValue());
			// Single CharacteristicValue for now. TODO Extend.
			Node myNode = findMyNodeAtThisLevel(node);
			if (myNode != null) {
				if (!processMyNode(individual, myNode)) {
					anyErrors = true;
				}
			} else {
				log
						.error(String
								.format(
										"No Random Number Generator Seed found for Individual %1$s",
										individual.getLabel()));
				throw new CDMConfigurationException(
						"No Random Number Generator Seed found.");
			}
		}
		return !anyErrors;
	}

	private boolean processMyNode(Individual individual, Node myNode)
			throws CDMConfigurationException, NumberFormatException,
			CDMRunException {
		boolean success = false;
		/* changed by Hendriek as the old version returned only null values of random seed
		 * old version:
		 * String myValue = myNode.getNodeValue();
		 */
		NamedNodeMap myAttributes = myNode.getAttributes();
		Node valueNode=null;
		if (myAttributes != null) {
		valueNode =myAttributes.getNamedItem("vl");
		if (valueNode == null) {
			valueNode =myAttributes.getNamedItem("value");
			if (valueNode == null) {
				
					
							throw new CDMConfigurationException(
									"Randomseed value not present");
						}
					}
					
		}
		String myValue = valueNode.getNodeValue();
		
		/* end changes Hendriek */
		if (myValue != null) {
			Matcher matcher = longPattern.matcher(myValue);
			if (matcher.matches()) {
				individual.setRandomNumberGeneratorSeed(Long.valueOf(myValue));
			} else {
				log
				.error(String
						.format(
								"Non float Random Number Generator Seed found for Individual %1$s",
								individual.getLabel()));

				throw new CDMConfigurationException(
						"Non float Random Number Generator Seed found.");
			}
		}
		
		return success;
	}
}
