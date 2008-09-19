package nl.rivm.emi.cdm.stax;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public abstract class AbstractStAXElementEventConsumer extends AbstractStAXEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	protected String xmlElementName = null;

	public AbstractStAXElementEventConsumer(String xmlElementName) {
		super();
		this.xmlElementName = xmlElementName;
		log.debug("Constructing, " + xmlElementName);
	}
	public String getXmlElementName() {
		return xmlElementName;
	}

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @throws  
	 * @throws UnexpectedFileStructureException 
	 * @throws XMLStreamException 
	 * @throws Exception
	 * @throws CDMConfigurationException
	 */
	abstract public void consumeEvents(XMLEventReader reader, AbstractStAXEventConsumer mother) throws XMLStreamException, UnexpectedFileStructureException;

	protected boolean elementPreCheck(XMLEventReader reader) throws XMLStreamException, UnexpectedFileStructureException  {
		boolean eventOK = false;
		XMLEvent event = reader.peek();
		String elementName = ((StartElement) event).getName().getLocalPart();
		if ((elementName != null) && (xmlElementName.equals(elementName))) {
			log.debug("Handling \"" + elementName + "\" element.");
			eventOK = true;
		} else {
			throw new UnexpectedFileStructureException("Unexpected element name " + elementName);
		}
		return eventOK;
	}

	/**
	 * Method assumes calling consumer has aligned reader on a StartElement
	 * XMLEvent.
	 * 
	 * @param reader
	 * @param xmlElementName
	 * @return
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException 
	 * @throws UnexpectedFileStructureException 
	 */
	protected abstract void handleElement(XMLEventReader reader, AbstractStAXEventConsumer mother)
	throws XMLStreamException, UnexpectedFileStructureException ;

	protected boolean elementPostCheck(XMLEventReader reader) throws UnexpectedFileStructureException, XMLStreamException {
		XMLEvent event = reader.peek();
		if (event.isEndElement()) {
			String elementName = ((EndElement) event).getName().getLocalPart();
			if ((elementName != null) && elementName.equals(xmlElementName)) {
				event = reader.nextEvent();
				log.info(xmlElementName + " ends");
			} else {
				//throw new UnexpectedFileStructureException(
				log.error(
						"Asymmetric end element event, expected "
								+ xmlElementName + ", got " + elementName);
			}
		} else {
//			throw new UnexpectedFileStructureException(
			log.error(
					"Wrong event type, expected " + XMLEvent.END_ELEMENT
							+ " pop, got " + event.getEventType());
		}
		return false;
	}
}