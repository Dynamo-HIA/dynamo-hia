package nl.rivm.emi.cdm.population;

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
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class StAXPopulationEventStreamer {
	Log log = LogFactory.getLog(this.getClass().getName());

	static public void streamEvents(Population population, XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException {
XMLEvent event = eventFactory.createStartElement("","",Population.xmlElementName);
writer.add(event);
Attribute attribute = eventFactory.createAttribute("lb", population.getLabel());
writer.add(attribute);
for (Individual individual : population) {
StAXIndividualEventStreamer.streamEvents(individual, 0, writer, eventFactory);
}
event = eventFactory.createEndElement("","",Population.xmlElementName);
writer.add(event);
}




}