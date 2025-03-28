package nl.rivm.emi.cdm.characteristic.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.XMLConfiguredObjectFactory;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.types.CompoundCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.NumericalContinuousCharacteristicType;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
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
			throws CDMConfigurationException, NumberFormatException,
			CDMRunException {
		boolean anyErrors = false;
		if (node != null) {
			// log.debug("Passed Node, name: " + node.getNodeName() + " value: "
			// + node.getNodeValue());
			// Single CharacteristicValue for now. TODO Extend.
			Node myNode = findMyNodeAtThisLevel(node);
			int numNodesFound = 0;
			while (myNode != null) {
				// log.debug("Processing node " + myNode.getNodeName());
				numNodesFound++;
				if (!processMyNode(individual, myNode, numberOfSteps)) {
					log.debug("Errors!");
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
			int numberOfSteps) throws CDMConfigurationException,
			NumberFormatException, CDMRunException {

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
			// log.debug("Node index " + index);
			CharacteristicsConfigurationMapSingleton charConfig = CharacteristicsConfigurationMapSingleton
					.getInstance();
			if (charConfig.isEmpty()) {
				throw new CDMConfigurationException(
						CDMConfigurationException.characteristicsConfigurationNotInitializedMessage);
			}
			Characteristic characteristic = charConfig
					.getCharacteristic(Integer.parseInt(index));
			/* gewijzigd door Hendriek: compound type toegevoegd; */
			if (!(characteristic.getType() instanceof NumericalContinuousCharacteristicType)
					&& !(characteristic.getType() instanceof CompoundCharacteristicType)) {
				success = handleIntegerValue(individual, numberOfSteps,
						success, myAttributes, index);
				/* gewijzigd door Hendriek: compound type toegevoegd; */
			} else if (characteristic.getType() instanceof NumericalContinuousCharacteristicType) {
				success = handleFloatValue(individual, numberOfSteps, success,
						myAttributes, index);

			} else if (characteristic.getType() instanceof CompoundCharacteristicType) {

				success = handleCompoundElementValue(individual, numberOfSteps,
						success, myAttributes, index, characteristic
								.getNumberOfElements());

			}

		} else {
			throw new CDMConfigurationException(
					"CharacteristicValue without attributes found.");
		}
		return success;

	}

	private boolean handleIntegerValue(Individual individual,
			int numberOfSteps, boolean success, NamedNodeMap myAttributes,
			String index) throws CDMConfigurationException, CDMRunException {
		Matcher matcher;
		boolean indexOK = isIndexPosInt(index);
		Node valueNode = myAttributes.getNamedItem("vl");
		if (valueNode == null) {
			valueNode = myAttributes.getNamedItem("value");
			if (valueNode == null) {
				throw new CDMConfigurationException(
						"CharacteristicValue without value attribute found.");
			}
		}
		String value = valueNode.getNodeValue();
		// log.debug("Node integer value " + value);
		if (indexOK) {
			success = addIntegerCharacteristicValue2Individual(individual,
					numberOfSteps, success, index, indexOK, value);
		} else {
			log.warn("CharacteristicValue attribute(s) no integer.");
		}
		return success;
	}

	public boolean addIntegerCharacteristicValue2Individual(
			Individual individual, int numberOfSteps, boolean success,
			String index, boolean indexOK, String value) throws CDMRunException {
		Matcher matcher;
		matcher = posIntPattern.matcher(value);
		boolean valueOK = matcher.matches();
		if (valueOK) {

			IntCharacteristicValue intCharacteristicValue = new IntCharacteristicValue(
					numberOfSteps, Integer.parseInt(index));
			intCharacteristicValue.appendValue(Integer.parseInt(value));

			/* end changes */
			// log.debug("Setting CharacteristicValue "
			// + intCharacteristicValue.getValue()
			// + " in Individual at index "
			// + +intCharacteristicValue.getIndex());
			individual.luxeSet(Integer.parseInt(index), intCharacteristicValue);
			success = true;
		} else {
			log.warn("CharacteristicValue attribute(s) no integer.");
		}
		return success;
	}

	private boolean handleFloatValue(Individual individual, int numberOfSteps,
			boolean success, NamedNodeMap myAttributes, String index)
			throws CDMConfigurationException, CDMRunException {
		Matcher matcher;
		boolean indexOK = isIndexPosInt(index);
		Node valueNode = myAttributes.getNamedItem("vl");
		if (valueNode == null) {
			valueNode = myAttributes.getNamedItem("value");
			if (valueNode == null) {
				throw new CDMConfigurationException(
						"CharacteristicValue without value attribute found.");
			}
		}
		String value = valueNode.getNodeValue();
		if (indexOK) {
			success = addFloatValue2Individual(individual, numberOfSteps,
					success, index, indexOK, value);
		} else {
			log.warn("CharacteristicValue attribute(s) not supported.");
		}
		return success;
	}

	public boolean addFloatValue2Individual(Individual individual,
			int numberOfSteps, boolean success, String index, boolean indexOK,
			String value) throws CDMRunException {
		// log.debug("Node float value " + value);
		// verwijderd door Hendriek omdat floatPattern geen exponentiele notatie
		// aankan
		// matcher = floatPattern.matcher(value);
		// boolean valueOK = matcher.matches();

		// if (valueOK) {
		/*
		 * this has be changed by hendriek to adapt for including newborns
		 */
		FloatCharacteristicValue floatCharacteristicValue = new FloatCharacteristicValue(
				numberOfSteps, Integer.parseInt(index));
		floatCharacteristicValue.appendValue(Float.parseFloat(value));

		;

		/* end changes */
		// log.debug("Setting CharacteristicValue "
		// + floatCharacteristicValue.getValue()
		// + " in Individual at index "
		// + +floatCharacteristicValue.getIndex());
		individual.luxeSet(Integer.parseInt(index), floatCharacteristicValue);
		success = true;
		return success;
	}

	/* added by Hendriek */
	private boolean handleCompoundElementValue(Individual individual,
			int numberOfSteps, boolean success, NamedNodeMap myAttributes,
			String index, int numberOfElements)
			throws CDMConfigurationException, CDMRunException {
		Matcher matcher;
		boolean indexOK = isIndexPosInt(index);
		Node valueNode = myAttributes.getNamedItem("vl");
		if (valueNode == null) {
			valueNode = myAttributes.getNamedItem("value");
			if (valueNode == null) {
				throw new CDMConfigurationException(
						"CharacteristicValue without value attribute found.");
			}
		}
		String value = valueNode.getNodeValue();
		// log.debug("Node float value " + value);

		if (indexOK) {

			if (Integer.parseInt(index) < individual.size()) {
				CompoundCharacteristicValue compoundCharacteristicValue = (CompoundCharacteristicValue) individual
						.get(Integer.parseInt(index));
				compoundCharacteristicValue.appendInitialValue(Float
						.parseFloat(value));
				individual.luxeSet(Integer.parseInt(index),
						compoundCharacteristicValue);
				// log.debug("Setting CharacteristicValue "
				// + compoundCharacteristicValue.getLastValue()
				// + " in Individual at index "
				// + compoundCharacteristicValue.getIndex());
				success = true;
			} else {
				CompoundCharacteristicValue compoundCharacteristicValue = new CompoundCharacteristicValue(
						numberOfSteps, Integer.parseInt(index),
						numberOfElements);
				compoundCharacteristicValue.appendInitialValue(Float
						.parseFloat(value));

				// log.debug("Setting CharacteristicValue "
				// + compoundCharacteristicValue.getLastValue()
				// + " in Individual at index "
				// + compoundCharacteristicValue.getIndex());
				individual.luxeSet(Integer.parseInt(index),
						compoundCharacteristicValue);
				success = true;
			}
		} else {
			log.warn("CharacteristicValue attribute(s) not supported.");
		}
		return success;
	}

	private boolean isIndexPosInt(String index) {
		Matcher matcher = posIntPattern.matcher(index);
		boolean indexPosInt = matcher.matches();
		matcher = zeroPattern.matcher(index);
		boolean indexZero = matcher.matches();
		boolean indexOK = indexPosInt && !indexZero;
		return indexOK;
	}
}