package nl.rivm.emi.dynamo.data.types.interfaces;

import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * A Class implementing this interface can handle part of the configuration.
 * 
 * @author mondeelr
 */
public interface IPureXMLHandlingLayer<T> {
	abstract T handle(ConfigurationNode node)
			throws ConfigurationException;

	public void streamEvents(T value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException;

}
