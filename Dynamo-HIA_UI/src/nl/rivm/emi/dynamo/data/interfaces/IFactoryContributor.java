package nl.rivm.emi.dynamo.data.interfaces;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;

/**
 * 
 * @author mondeelr
 * 
 */
public interface IFactoryContributor {
	public void manufactureDefault();
}
