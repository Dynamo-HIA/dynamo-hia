package nl.rivm.emi.cdm.prngutil;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.IndividualStAXEventsConsumer;
import nl.rivm.emi.cdm.obsolete.NopStAXEventConsumerBase;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationStAXEventConsumer;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.stax.AbstractStAXElementEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RandomSeedStAXEventConsumer extends
		AbstractStAXElementEventConsumer {

	Log log = LogFactory.getLog(this.getClass().getName());

	String xmlElementName = null;

	public RandomSeedStAXEventConsumer(String xmlElementName) {
		super(xmlElementName);
		log.debug("Constructing, " + xmlElementName);
	}

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @throws UnexpectedFileStructureException
	 * @throws XMLStreamException
	 * @throws CDMConfigurationException
	 */
	public void consumeEvents(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		if (elementPreCheck(reader)) {
			handleElement(reader, mother);
		}
	}

	@Override
	protected void handleElement(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		XMLEvent event = reader.nextEvent();
		log.debug(xmlElementName + "!!!");
		Attribute valueAttribute = ((StartElement) event)
				.getAttributeByName(new QName("vl"));
		if (valueAttribute == null) {
			throw new UnexpectedFileStructureException(
					"No \"vl\" attribute found for \"" + xmlElementName
							+ "\" element.");
		}
		String valueString = valueAttribute.getValue();
		Long valueLong = Long.decode(valueString);
		((IndividualStAXEventsConsumer) mother).addRNGSeed(valueLong);
	}

	@Override
	public void add(Object theObject) {
		// TODO Auto-generated method stub

	}

}
