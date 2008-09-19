package nl.rivm.emi.dynamo.data.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;

public class CopyOfStAXEntryPoint {
	static Log log = LogFactory.getLog("nl.rivm.emi.cdm.StAXEntryPoint");

	/**
	 * Expects an XML-file. When the XML-file contains a hierarchy of elements
	 * the Factory-Classes stored in the FactoryMap know how to handle a
	 * Hierarchy of Objects is returned. Null if this is not the case or when
	 * the XML content caues errors to happen.
	 * 
	 * @param populationFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws CDMConfigurationException
	 */
	// static public Object processFile(File populationFile)
	// throws FileNotFoundException, XMLStreamException,
	// UnexpectedFileStructureException {
	// Object whatEver = null;
	// XMLInputFactory factory = XMLInputFactory.newInstance();
	// Reader fileReader = new FileReader(populationFile);
	// XMLEventReader reader = factory.createXMLEventReader(fileReader);
	// if (reader.peek() != null) { // Something to do.
	// PopulationDocumentStAXEventConsumer popCons = new
	// PopulationDocumentStAXEventConsumer();
	// popCons.consumeEvents(reader, null);
	// whatEver = popCons.getPopulation();
	// } else {
	// throw new UnexpectedFileStructureException("No events in File "
	// + populationFile.getName());
	// }
	// return whatEver;
	// }
	/**
	 * 
	 * @param population
	 * @param populationFile
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws IOException
	 */
	static public void produceFile(Object theModel, File configurationFile)
			throws XMLStreamException, UnexpectedFileStructureException,
			IOException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		Writer fileWriter;
		fileWriter = new FileWriter(configurationFile);
		XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			StAXDocumentEventStreamer documentStreamer = new StAXDocumentEventStreamer();
			documentStreamer.streamEvents(theModel, writer, eventFactory);
	}
}
