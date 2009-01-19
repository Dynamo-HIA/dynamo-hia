package nl.rivm.emi.dynamo.data.interfaces;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;

/**
 * Marker interface for Dynamo-HIA configuration Objects that are able to write
 * out their content.
 * For now there is a group of Objects that relies on an external factory.
 * 
 * @author mondeelr
 * 
 */
public interface IStaxEventContributor {
	public void streamEvents(XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException,
			UnexpectedFileStructureException, IOException;

}
