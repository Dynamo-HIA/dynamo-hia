package nl.rivm.emi.cdm.individual;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Population;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class IndElementStAXEventConsumer {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.stax.DocumentStAXEventConsumer");

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @return
	 * @throws CDMConfigurationException
	 */
	static public Object dispatchEvents(XMLEventReader reader) {
		Object whatEver = null;
		XMLEvent event;
		try {
			event = reader.peek();
			if ((event != null) && event.isStartElement()) {
				String elementName = ((StartElement) event).getName()
						.getLocalPart();
				if ("ind".equals(elementName)) {
					event = reader.nextEvent();
					System.out.println("Ind!!!");
					event = reader.peek();
					// Delegating goes here....
					whatEver = new Individual("String","String");
					if ((event != null) && event.isEndElement()) {
						elementName = ((EndElement) event).getName()
								.getLocalPart();
						if ("pop".equals(elementName)) {
							event = reader.nextEvent();
							System.out.println("Pop ends");
						} else {
							whatEver = new Exception(
									"Asymmetric end element event, expected for pop, got for "
											+ elementName);
						}
					} else {
						if (event != null) {
							whatEver = new Exception("Unexpected eventType "
									+ event.getEventType()
									+ ", expected end document ("
									+ XMLEvent.END_DOCUMENT + ").");
						}
					}
				} else {
					whatEver = new Exception("Unexpected element name "
							+ elementName);
				}
			}
		} catch (Exception e) {
			whatEver = e;
		} finally {
			return whatEver;
		}
	}
}
