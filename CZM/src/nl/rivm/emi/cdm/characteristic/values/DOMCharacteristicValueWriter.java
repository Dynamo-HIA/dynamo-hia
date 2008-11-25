package nl.rivm.emi.cdm.characteristic.values;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

public class DOMCharacteristicValueWriter {
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

	public static void generateDOM(
			FloatCharacteristicValue characteristicValue, int stepNumber,
			Element parentElement) throws ParserConfigurationException {
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

	/* added by Hendriek */
	public static void generateDOM(
			CompoundCharacteristicValue characteristicValue, int stepNumber,
			Element parentElement) throws ParserConfigurationException {
		String elementName = characteristicValue.getElementName();

		int index = characteristicValue.getIndex();
		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
		}
		float[] value = characteristicValue.getUnwrappedValue(stepNumber);

		for (int i = 0; i < value.length; i++) {
			Element element = parentElement.getOwnerDocument().createElement(
					elementName);
			element.setAttribute("id", new Integer(index).toString());
			element.setAttribute("vl", new Float(value[i]).toString());
			parentElement.appendChild(element);
		}
	}

}
