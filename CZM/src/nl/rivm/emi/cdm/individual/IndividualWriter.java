package nl.rivm.emi.cdm.individual;

import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.characteristic.DOMCharacteristicValueInserter;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
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
		Iterator<CharacteristicValueBase> iterator = individual.iterator();
		while (iterator.hasNext()) {
			CharacteristicValueBase charVal = iterator.next();
			if(charVal instanceof IntCharacteristicValue){
			DOMCharacteristicValueInserter.generateDOM((IntCharacteristicValue)charVal, stepNumber, element);
			} else {
				if(charVal instanceof FloatCharacteristicValue){
					DOMCharacteristicValueInserter.generateDOM((FloatCharacteristicValue)charVal, stepNumber, element);
				}
				}
			}
		Node textNode = parentElement.getOwnerDocument().createTextNode("\n");
		parentElement.appendChild(textNode);
	}
}