package nl.rivm.emi.cdm.individual;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.StAXCharacteristicValueEventStreamer;
import nl.rivm.emi.cdm.prngutil.StAXRNGSeedEventStreamer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class StAXIndividualEventStreamer {
	Log log = LogFactory.getLog(this.getClass().getName());

	static public void streamEvents(Individual individual,
			int step, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
			XMLEvent event = eventFactory.createStartElement("", "",
					Individual.xmlElementName);
			writer.add(event);
			Attribute attribute = eventFactory.createAttribute("lb", individual
					.getLabel());
			writer.add(attribute);
			StAXRNGSeedEventStreamer.streamEvents(individual, writer, eventFactory);
for(CharacteristicValueBase characteristicValue : individual){
	StAXCharacteristicValueEventStreamer.streamEvents(characteristicValue, step, writer, eventFactory);
}
			event = eventFactory.createEndElement("", "",
					Individual.xmlElementName);
			writer.add(event);
			event = eventFactory.createCharacters("\n");
			writer.add(event);
	}

}