package nl.rivm.emi.cdm.population;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.StAXIndividualEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXElementEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class PopulationStAXEventConsumer extends
		AbstractStAXElementEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	private Population population;

	public PopulationStAXEventConsumer(String xmlElementName) {
		super(xmlElementName);
		log.debug("Constructing, " + xmlElementName);
	}

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @throws UnexpectedFileStructureException
	 * @throws XMLStreamException
	 * @throws Exception
	 * @throws CDMConfigurationException
	 */
	public void consumeEvents(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		logHeadOfEventStream(reader, "");
		if (elementPreCheck(reader)) {
			handleElement(reader, mother);
		}
	}

	/**
	 * Method assumes calling consumer has aligned reader on a StartElement
	 * XMLEvent.
	 * 
	 * @param reader
	 * @param elementName
	 * @param mother
	 * @return
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 */
	protected void handleElement(XMLEventReader reader, AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		XMLEvent event;
		event = reader.nextEvent();
		Attribute labelAttribute = ((StartElement) event)
				.getAttributeByName(new QName("lb"));
		if (labelAttribute == null) {
			throw new UnexpectedFileStructureException(
					"No \"lb\" attribute fount \"pop\" element.");
		}
		String label = labelAttribute.getValue();
		population = new Population(xmlElementName, label);
		mother.add(population);
		while (nextStartElementOrFalse(reader)) {
			event = reader.peek();
			StAXIndividualEventConsumer indiCons = new StAXIndividualEventConsumer(
					"ind");
			indiCons.consumeEvents(reader, this);
//			if (!elementPostCheck(reader)) {
//				throw new UnexpectedFileStructureException("Population file.");
//			}
		}
		if(population.size() == 0) {
			throw new UnexpectedFileStructureException(
					"No individuals found in Population.");
		}
	}

	@Override
	public void add(Object theObject) {
		population.add((Individual) theObject);
		// TODO Auto-generated method stub

	}
}