package nl.rivm.emi.cdm.characteristic;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

public class DOMCharacteristicValueInserter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.characteristic.CharacteristicWriter");

	public static void generateDOM(IntCharacteristicValue characteristicValue,
			int stepNumber, Element parentElement)
			throws ParserConfigurationException {
		String elementName = characteristicValue.getElementName();
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		int index = characteristicValue.getIndex();
		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
		}
		int value = characteristicValue.getValue(stepNumber);
		element.setAttribute("id", new Integer(index).toString());
		element.setAttribute("vl", new Integer(value).toString());
		parentElement.appendChild(element);
	}

	public static void generateDOM(FloatCharacteristicValue characteristicValue,
			int stepNumber, Element parentElement)
			throws ParserConfigurationException {
		String elementName = characteristicValue.getElementName();
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		int index = characteristicValue.getIndex();
		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
		}
		float value = characteristicValue.getValue(stepNumber);
		element.setAttribute("id", new Integer(index).toString());
		element.setAttribute("vl", new Float(value).toString());
		parentElement.appendChild(element);
	}
}
