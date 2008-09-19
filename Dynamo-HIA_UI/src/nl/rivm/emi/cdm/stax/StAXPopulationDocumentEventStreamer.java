package nl.rivm.emi.cdm.stax;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class StAXPopulationDocumentEventStreamer {
	Log log = LogFactory.getLog(this.getClass().getName());

	private Population population = null;

	public StAXPopulationDocumentEventStreamer() {
		super();
	}

	public void add(Object population) {
		this.population = (Population) population;
	}

	/**
	 * @throws XMLStreamException
	 */
	static public void streamEvents(Population population,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		StAXPopulationEventStreamer.streamEvents(population, writer,
				eventFactory);
		writer.flush();
		writer.close();
	}

	public Object getPopulation() {
		return population;
	}
}
