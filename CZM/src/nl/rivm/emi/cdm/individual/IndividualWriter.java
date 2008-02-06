package nl.rivm.emi.cdm.individual;

import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.characteristic.CharacteristicWriter;
import nl.rivm.emi.cdm.characteristic.IntCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual.CharacteristicValueIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IndividualWriter {

	Log log = LogFactory.getLog(getClass().getName());

	public IndividualWriter() {
		super();
	}

	public static void generateDOM(Individual individual, int stepNumber, Element parentElement)
			throws ParserConfigurationException {
		String elementName = individual.getElementName();
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		String label = individual.getLabel();
		if (label != null && !"".equals(label)) {
			element.setAttribute("lb", label);
		}
		parentElement.appendChild(element);
		Iterator<IntCharacteristicValue> iterator = individual.iterator();
		while (iterator.hasNext()) {
			IntCharacteristicValue charVal = iterator.next();
			CharacteristicWriter.generateDOM(charVal, stepNumber, element);
		}
		// TODO No todo, but layout fluff.
		Node textNode = parentElement.getOwnerDocument().createTextNode("\n");
		parentElement.appendChild(textNode);
	}
}