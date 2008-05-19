package nl.rivm.emi.cdm.individual;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueStAXEventsConsumer;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.prngutil.RandomSeedStAXEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXElementEventConsumer;
import nl.rivm.emi.cdm.stax.AbstractStAXEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndividualStAXEventsConsumer extends
		AbstractStAXElementEventConsumer {

	Log log = LogFactory.getLog(this.getClass().getName());

	private Individual currentIndividual;

	public IndividualStAXEventsConsumer(String xmlElementName) {
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

	protected void handleElement(XMLEventReader reader,
			AbstractStAXEventConsumer mother) throws XMLStreamException,
			UnexpectedFileStructureException {
		XMLEvent event;
		String elementName = null;
		event = reader.nextEvent();
		log.debug(xmlElementName + "!!!");
		Attribute labelAttribute = ((StartElement) event)
				.getAttributeByName(new QName("lb"));
		if (labelAttribute == null) {
			throw new UnexpectedFileStructureException(
					"No \"lb\" attribute found for \"" + xmlElementName
							+ "\" element.");
		}
		String label = labelAttribute.getValue();
		currentIndividual = new Individual(xmlElementName, label);
		mother.add(currentIndividual);
		while (nextStartElementOrFalse(reader)) {
			elementName = ((StartElement) event).getName().getLocalPart();
			if ("rngseed".equals(elementName)) {
				handleRandomNumberGeneratorSeed(reader);
			} else {
				if ("ch".equals(elementName)) {
					handleCharacteristicValue(reader);
				} else {
					throw new UnexpectedFileStructureException(
							"Unexpected subelement \"" + elementName
									+ "\" in \"" + xmlElementName
									+ "\" element.");
				}
			}
		}
		if (!elementPostCheck(reader)) {
			throw new UnexpectedFileStructureException(
					"Asymmetric end element event for element \""
							+ xmlElementName + "\", got \"" + elementName
							+ "\"");
		}
	}

	private void handleRandomNumberGeneratorSeed(XMLEventReader reader) throws XMLStreamException, UnexpectedFileStructureException {
		System.out.println("Delegating rngseed.");
		RandomSeedStAXEventConsumer seedCons = new RandomSeedStAXEventConsumer(
				"rngseed");
		seedCons.consumeEvents(reader, this);
	}

	private void handleCharacteristicValue(XMLEventReader reader) throws XMLStreamException, UnexpectedFileStructureException {
		System.out.println("Delegating ch.");
		CharacteristicValueStAXEventsConsumer charCons = new CharacteristicValueStAXEventsConsumer(
				"ch");
		charCons.consumeEvents(reader, this);
	}

	@Override
	public void add(Object theObject) {
		// TODO Auto-generated method stub
	}

	public void addRNGSeed(Long seed) {
		if (currentIndividual.getRandomNumberGeneratorSeed() != null) {
			log
					.error("More than one RandomNumberGeneratorSeed, using last one.");
		}
		currentIndividual.setRandomNumberGeneratorSeed(seed);
	}

	public void addCharacteristicValue(CharacteristicValueBase cvb) {
		int index = cvb.getIndex();
		if (currentIndividual.get(index) != null) {
			log.error("More than one CharacteristicValue at index " + index
					+ ", using last one.");
		}
		currentIndividual.set(cvb.getIndex(), cvb);
	}
}
