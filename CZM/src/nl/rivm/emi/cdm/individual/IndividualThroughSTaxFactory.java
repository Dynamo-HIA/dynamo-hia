package nl.rivm.emi.cdm.individual;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueFactory;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.prngutil.PRngSeedFactory;

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
public class IndividualThroughSTaxFactory extends XMLConfiguredObjectFactory {
	Log log = LogFactory.getLog(getClass().getName());

	public IndividualThroughSTaxFactory(String elementName) {
		super(elementName);
	}

	/**
	 * Try to build Individuals taking the passed Node in a DOM-tree as a
	 * startingpoint. The level of this Node is scanned for the required
	 * elementName. The result is considered error-free when only Individuals
	 * with a valid CharacteristicValue have been added to the Population.
	 * 
	 * @param node
	 * @param Population
	 *            to put Individuals into.
	 * @throws CZMConfigurationException
	 * @throws CDMRunException
	 * @throws NumberFormatException
	 */
	public boolean makeIt(Node node, Population population, int numberOfSteps)
			throws CDMConfigurationException, NumberFormatException,
			CDMRunException {
		boolean noErrors = true;
		int numberOfValidIndividuals = 0;
		if (node != null) {
			log.info("Passed Node, name: " + node.getNodeName() + " value: "
					+ node.getNodeValue());
			Node myNode = findMyNodeAtThisLevel(node);
			while (myNode != null) {
				String label = tryToFindLabel(myNode);
				Individual currentIndividual = new Individual("ind", label);
				log.info("Individual " + label);
				Node childNode = myNode.getFirstChild();
				PRngSeedFactory seedFactory = new PRngSeedFactory(
				"rngseed");
				boolean success = seedFactory.makeIt(childNode,
						currentIndividual);
				CharacteristicValueFactory charValFactory = new CharacteristicValueFactory(
						"ch");
				success = charValFactory.makeIt(childNode,
						currentIndividual, numberOfSteps);
				// Do not add individuals without a charval.
				if (!success) {
					log.warn("CharacteristicValue factoring went awry");
					noErrors = false;
				} else {
					numberOfValidIndividuals++;
					log.debug("Adding Individual " + numberOfValidIndividuals
							+ " to Population");
					population.addIndividual(currentIndividual);
				}
				myNode = findMyNextNodeAtThisLevel(myNode);
			}
		}
		if (numberOfValidIndividuals == 0) {
			noErrors = false;
		} else {
			;
		}
		return noErrors;
	}

	private String tryToFindLabel(Node myNode) {
		String label = "Anonymous";
		NamedNodeMap myAttributes = myNode.getAttributes();
		if (myAttributes != null) {
			Node labelNode = myAttributes.getNamedItem("lb");
			if (labelNode == null) {
				labelNode = myAttributes.getNamedItem("label");
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
