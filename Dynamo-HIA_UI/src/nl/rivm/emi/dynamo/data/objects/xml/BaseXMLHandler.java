package nl.rivm.emi.dynamo.data.objects.xml;

/**
 * Handler for 
 * <classes>
 * 	<class>
 * 		<index>1</index>
 * 		<name>jan</name>
 * 	</class>
 * 	.......
 * </classes>
 * XML fragments.
 */
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class BaseXMLHandler<T> {

	protected final XMLValueConverter<T> myConverter;
	protected final String xmlElementName;

	public BaseXMLHandler(String xmlElementName,
			XMLValueConverter<T> theConverter) {
		super();
		this.xmlElementName = xmlElementName;
		myConverter = theConverter;
	}

	final public T handle(ConfigurationNode node) throws ConfigurationException {
		T result = null;
		if (xmlElementName.equals(node.getName())) {
			Object valueObject = node.getValue();
			if (valueObject != null) {
				if (valueObject instanceof String) {
					String valueString = (String) valueObject;

					if (valueString != "") {
						result = myConverter.convert(valueString);
						if (result == null) {
							throw new ConfigurationException(
									"Non supported tag value: " + valueString);
						}
					} else {
						throw new ConfigurationException("Tag has empty value.");
					}
				} else {
					throw new ConfigurationException(
							"Tag has non String value.");
				}
			} else {
				throw new ConfigurationException("Tag has null value.");
			}
		} else {
			throw new ConfigurationException("Incorrect tag for this handler.");
		}
		return result;
	}

	final public void streamEvents(T value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		XMLEvent event = eventFactory
				.createStartElement("", "", xmlElementName);
		writer.add(event);
		event = eventFactory.createCharacters(myConverter.streamValue(value));
		writer.add(event);
		event = eventFactory.createEndElement("", "", xmlElementName);
		writer.add(event);
	}
}