package nl.rivm.emi.cdm.population;

import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.individual.IndividualFactory;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PopulationFactory extends XMLConfiguredObjectFactory {

	Log log = LogFactory.getLog(getClass().getName());

	public PopulationFactory(String elementName) {
		super(elementName);
	}

	/**
	 * Try to build a Population taking the passed Node in a DOM-tree as a
	 * startingpoint. The level of this Node is scanned for the required
	 * elementName. The result is considered error-free when only Individuals
	 * with a valid CharacteristicValue have been added to the Population.
	 * 
	 * @param node
	 * @param numberOfSteps TODO
	 * @param Simulation
	 *            to put Population into.
	 * @throws CZMConfigurationException
	 * @throws CZMRunException 
	 * @throws NumberFormatException 
	 */
	public Population makeItFromDOM(Node node, int numberOfSteps)
			throws CZMConfigurationException, NumberFormatException, CZMRunException {
		Population resultPopulation = null;
		if (node != null) {
			log.info("Passed Node, name: " + node.getNodeName() + " value: "
					+ node.getNodeValue());
			Node myNode = findMyNodeAtThisLevel(node);
			// while (myNode != null) {
			if (myNode != null) {
				String label = tryToFindLabel(myNode);
				resultPopulation = new Population(getElementName(),
						label);
				Node childNode = myNode.getFirstChild();
				IndividualFactory individualFactory = new IndividualFactory(
						"ind");
				boolean success = individualFactory.makeIt(childNode,
						resultPopulation, numberOfSteps);
				// Do not add individuals without a charval.
				if (!success) {
					log.warn("Individual factoring had errors.");
					resultPopulation = null;
				}
				myNode = findMyNextNodeAtThisLevel(myNode);
				if (myNode != null) {
					log.warn("Only one Population per Simulation allowed.");
				}
			}
		}
		return resultPopulation;
	}

	private String tryToFindLabel(Node myNode) {
		String label = "Anonymous";
		NamedNodeMap myAttributes = myNode.getAttributes();
		if (myAttributes != null) {
			Node labelNode = myAttributes.getNamedItem("label");
			if (labelNode == null) {
				labelNode = myAttributes.getNamedItem("lb");
			}
			if (labelNode == null) {
				log.info("Individual with strange attributes found.");
			} else {
				label = labelNode.getNodeValue();
			}
		}
		return label;
	}

}
