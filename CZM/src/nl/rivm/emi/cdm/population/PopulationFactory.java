package nl.rivm.emi.cdm.population;

import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.individual.IndividualFactory;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;
import nl.rivm.emi.cdm.simulation.Simulation;

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
	 * @param Simulation
	 *            to put Population into.
	 * @throws CZMConfigurationException
	 * @throws CZMRunException 
	 * @throws NumberFormatException 
	 */
	public boolean makeIt(Node node, Simulation simulation)
			throws CZMConfigurationException, NumberFormatException, CZMRunException {
		boolean noErrors = true;
		if (node != null) {
			log.info("Passed Node, name: " + node.getNodeName() + " value: "
					+ node.getNodeValue());
			Node myNode = findMyNodeAtThisLevel(node);
			// while (myNode != null) {
			if (myNode != null) {
				String label = tryToFindLabel(myNode);
				Population currentPopulation = new Population(getElementName(),
						label);
				Node childNode = myNode.getFirstChild();
				IndividualFactory individualFactory = new IndividualFactory(
						"ind");
				boolean success = individualFactory.makeIt(childNode,
						currentPopulation, simulation.getNumberOfSteps());
				// Do not add individuals without a charval.
				if (!success) {
					log.warn("Individual factoring had errors.");
					noErrors = false;
				} else {
					log.debug("Adding Population to Simulation.");
					simulation.setPopulation(currentPopulation);
				}
				myNode = findMyNextNodeAtThisLevel(myNode);
				if (myNode != null) {
					log.warn("Only one Population per Simulation allowed.");
				}
			}
		}
		return noErrors;
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
