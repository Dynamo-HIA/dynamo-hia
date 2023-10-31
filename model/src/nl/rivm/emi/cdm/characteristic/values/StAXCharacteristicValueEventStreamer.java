package nl.rivm.emi.cdm.characteristic.values;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class StAXCharacteristicValueEventStreamer {
//	Log log = LogFactory.getLog(this.getClass().getName());

	static public void streamEvents(
			CharacteristicValueBase characteristicValue, int step,
			XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "",
				CharacteristicValueBase.xmlElementName);
		writer.add(event);
		Attribute attribute = eventFactory.createAttribute("id", Integer
				.toString(characteristicValue.getIndex()));
		writer.add(attribute);
		String valueString = determineValueString(characteristicValue, step);
		attribute = eventFactory.createAttribute("vl", valueString);
		writer.add(attribute);
		event = eventFactory.createEndElement("", "",
				CharacteristicValueBase.xmlElementName);
		writer.add(event);
	}

	private static String determineValueString(
			CharacteristicValueBase characteristicValue, int step) {
		String resultString = "Unknown CharacteristicValue type: "
				+ characteristicValue.getClass().getName();
		if (characteristicValue instanceof IntCharacteristicValue) {
			resultString = Integer
					.toString(((IntCharacteristicValue) characteristicValue)
							.getValue(step));
		} else {
			if (characteristicValue instanceof FloatCharacteristicValue) {
				resultString = Float
						.toString(((FloatCharacteristicValue) characteristicValue)
								.getValue(step));
			}
		}
		return resultString;
	}
}