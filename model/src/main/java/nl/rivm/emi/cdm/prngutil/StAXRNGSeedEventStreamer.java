package nl.rivm.emi.cdm.prngutil;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.StAXIndividualEventStreamer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr
 * 
 */
public class StAXRNGSeedEventStreamer {
	Log log = LogFactory.getLog(this.getClass().getName());

	static public void streamEvents(Individual individual,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "", "rngseed");
		writer.add(event);
		Attribute attribute = eventFactory.createAttribute("vl", (individual
				.getRandomNumberGeneratorSeed()).toString());
		writer.add(attribute);
		event = eventFactory.createEndElement("", "", "rngseed");
		writer.add(event);
	}
}