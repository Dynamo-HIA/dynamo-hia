package nl.rivm.emi.cdm.characteristic;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.individual.Individual;

import org.w3c.dom.Element;

public class CharacteristicWriter {
	public static void generateDOM(IntCharacteristicValue characteristicValue,
			int stepNumber, Element parentElement) throws ParserConfigurationException {
		String elementName = characteristicValue.getElementName();
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		int index = characteristicValue.getIndex();
		int value = characteristicValue.getValue(stepNumber);
		element.setAttribute("id", new Integer(index).toString());
		element.setAttribute("vl", new Integer(value).toString());
		parentElement.appendChild(element);
	}
}
