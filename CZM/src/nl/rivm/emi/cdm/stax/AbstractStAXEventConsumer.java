package nl.rivm.emi.cdm.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public abstract class AbstractStAXEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	public AbstractStAXEventConsumer() {
		super();
	}

	abstract public void add(Object theObject);

	abstract public void consumeEvents(XMLEventReader reader, AbstractStAXEventConsumer mother)
			throws XMLStreamException, UnexpectedFileStructureException;

	public boolean nextStartElementOrFalse(XMLEventReader reader) {
		boolean startElementEventFound = false;
		XMLEvent event;
		try {
			event = reader.peek();
			while ((event != null) && !event.isStartElement()) {
				event = reader.nextEvent();
				event = reader.peek();
			}
			startElementEventFound = true;
		} catch (XMLStreamException e) {
		} catch (ArrayIndexOutOfBoundsException ex) {
			log.error("Exception caught: : " + ex.getClass().getName()
					+ " with message: " + ex.getMessage());
		} catch (Exception ex) {
			log.error("Exception caught: : " + ex.getClass().getName()
					+ " with message: " + ex.getMessage());
		} finally {
			return startElementEventFound;
		}
	}
}