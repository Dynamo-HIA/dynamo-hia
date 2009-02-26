package nl.rivm.emi.dynamo.data.objects.layers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.FactoryEntryPoint;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

public abstract class StaxWriterEntryPoint extends FactoryEntryPoint {

	protected StaxWriterEntryPoint(RootElementNamesEnum rootElement, boolean observable){
		super(rootElement, observable);
	}

	// read
	// write
	public void writeToFile(File outputFile) throws XMLStreamException,
			UnexpectedFileStructureException, IOException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		Writer fileWriter;
		fileWriter = new FileWriter(outputFile);
		XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		streamEvents(writer, eventFactory);
		writer.flush();
	}

	public abstract void streamEvents(XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException;
}
