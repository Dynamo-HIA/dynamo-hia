package nl.rivm.emi.cdm.characteristic.values;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.IndividualStAXEventsConsumer;
import nl.rivm.emi.cdm.obsolete.NopStAXEventConsumerBase;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.stax.AbstractStAXElementEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacteristicValueStAXEventsConsumer extends
		AbstractStAXElementEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	public CharacteristicValueStAXEventsConsumer(String xmlElementName) {
		super(xmlElementName);
		log.debug("Constructing, " + xmlElementName);
	}

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws CDMConfigurationException
	 */
	public void consumeEvents(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		if (elementPreCheck(reader)) {
			handleMyElement(reader, mother);
		}
	}

	private void handleMyElement(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		XMLEvent event = reader.nextEvent();
		Attribute indexAttribute = ((StartElement) event)
				.getAttributeByName(new QName("id"));
		if (indexAttribute == null) {
			throw new UnexpectedFileStructureException(
					"No \"id\" attribute found for \"" + xmlElementName
							+ "\" element.");
		}
		String indexString = indexAttribute.getValue();
		Integer indexInteger = Integer.decode(indexString);

		Attribute valueAttribute = ((StartElement) event)
				.getAttributeByName(new QName("vl"));
		if (valueAttribute == null) {
			throw new UnexpectedFileStructureException(
					"No \"vl\" attribute found for \"" + xmlElementName
							+ "\" element.");
		}
		String valueString = valueAttribute.getValue();
		try{		CharacteristicValueBase charVal;
		int indexOfDot = valueString.indexOf(".");
		if (indexOfDot != -1) {
			Float floatValue = Float.parseFloat(valueString);
			charVal = new FloatCharacteristicValue(1, indexInteger);
			((FloatCharacteristicValue)charVal).appendValue(floatValue);
		} else {
			Integer intValue = Integer.parseInt(valueString);
			charVal = new IntCharacteristicValue(1, indexInteger);
			((IntCharacteristicValue) charVal).appendValue(intValue);
		}
		((IndividualStAXEventsConsumer)mother).addCharacteristicValue(charVal);
		} catch(CDMRunException e){
			throw new UnexpectedFileStructureException(e.getMessage());
		}
	}

	@Override
	protected void handleElement(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Object theObject) {
		// TODO Auto-generated method stub

	}
}