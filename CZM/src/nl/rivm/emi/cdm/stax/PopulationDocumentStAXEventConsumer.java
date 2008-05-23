package nl.rivm.emi.cdm.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationStAXEventConsumer;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class PopulationDocumentStAXEventConsumer extends
		AbstractStAXEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	private Population population = null;

	public PopulationDocumentStAXEventConsumer() {
		super();
	}

	public void add(Object population) {
		this.population = (Population) population;
	}

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @throws UnexpectedFileStructureException
	 * @throws XMLStreamException
	 * @throws Exception
	 * @throws Exception
	 * @throws CDMConfigurationException
	 */
	public void consumeEvents(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		logHeadOfEventStream(reader, "");
		XMLEvent event;
		if (((event = reader.peek()) != null) && (event.isStartDocument())) {
			event = reader.nextEvent(); // Get my event.
			// Preprocessing finished.
			if (nextStartElementOrFalse(reader)) {
				log.debug("Delegating root-element and everything below.");
				PopulationStAXEventConsumer popCons = new PopulationStAXEventConsumer(
						"pop");
				popCons.consumeEvents(reader, this);
				if (((event = reader.peek()) != null)

				&& (event.isEndDocument())) { // Something
					event = reader.nextEvent();
				} else {
					throw new UnexpectedFileStructureException(
							"Unexpected eventType " + event.getEventType()
									+ ", expected end document ("
									+ XMLEvent.END_DOCUMENT + ").");
				}
			} else {
				throw new UnexpectedFileStructureException(
						"No root element found.");
			}
		} else {
			throw new UnexpectedFileStructureException(
					"No start document found.");
		}
	}

	public Object getPopulation() {
		return population;
	}
}
