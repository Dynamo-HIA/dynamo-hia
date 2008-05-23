package nl.rivm.emi.cdm.characteristic.values;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.StAXIndividualEventConsumer;
import nl.rivm.emi.cdm.obsolete.NopStAXEventConsumerBase;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.stax.AbstractStAXElementEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StAXCharacteristicValueEventConsumer extends
		AbstractStAXElementEventConsumer {
	Log log = LogFactory.getLog(this.getClass().getName());

	// TODO Pass or something...
	final int NUMSTEPS = 10;
	public StAXCharacteristicValueEventConsumer(String xmlElementName) {
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
//		logHeadOfEventStream(reader, "entering consumeEvents");

		if (elementPreCheck(reader)) {
			handleElement(reader, mother);
		}
	}

	@Override
	protected void handleElement(XMLEventReader reader,
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
			charVal = new FloatCharacteristicValue(NUMSTEPS, indexInteger);
			((FloatCharacteristicValue)charVal).appendValue(floatValue);
		} else {
			Integer intValue = Integer.parseInt(valueString);
			charVal = new IntCharacteristicValue(NUMSTEPS, indexInteger);
			((IntCharacteristicValue) charVal).appendValue(intValue);
		}
		((StAXIndividualEventConsumer)mother).addCharacteristicValue(charVal);
//		logHeadOfEventStream(reader, "attributes processed");
		event = reader.nextEvent();
//		logHeadOfEventStream(reader, "one event fetched after attributes processed.");
		} catch(CDMRunException e){
			throw new UnexpectedFileStructureException(e.getMessage());
		}
	}


	@Override
	public void add(Object theObject) {
		// TODO Auto-generated method stub

	}
}