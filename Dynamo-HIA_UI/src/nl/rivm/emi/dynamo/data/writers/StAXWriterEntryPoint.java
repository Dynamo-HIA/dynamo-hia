package nl.rivm.emi.dynamo.data.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StAXWriterEntryPoint {
	static Log log = LogFactory.getLog("nl.rivm.emi.cdm.StAXEntryPoint");
	static Age utilityAge = new Age();
	static Sex utilitySex = new Sex();

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
		if (theModel != null) {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter;
			fileWriter = new FileWriter(configurationFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			streamDocumentEvents(theModel, writer, eventFactory);
		} else {
			log.info("Model is null, nothing to write.");
		}
	}

	/**
	 * @throws XMLStreamException
	 */
	static public void streamDocumentEvents(Object theModel,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		event = eventFactory.createCharacters("\n");
		writer.add(event);
		event = eventFactory.createStartElement("", "", "incidences");
		writer.add(event);
		event = eventFactory.createCharacters("\n");
		writer.add(event);
		if (theModel instanceof AgeMap) {
			streamAgeMapEvents((AgeMap) theModel, writer, eventFactory);
		}
		event = eventFactory.createEndElement("", "", "incidences");
		writer.add(event);
		event = eventFactory.createCharacters("\n");
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
		writer.flush();
		writer.close();
	}

	static public void streamAgeMapEvents(AgeMap<SexMap<IObservable>> theModel,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		TreeMap<String, String> contentMap = new TreeMap<String, String>();
		for (int ageCount = utilityAge.getMIN_VALUE(); ageCount < theModel.size(); ageCount++) {
			Integer ageCountInteger = new Integer(ageCount);
			contentMap.put(utilityAge.getElementName(), ageCountInteger.toString());
			SexMap<IObservable> sexMap = (SexMap<IObservable>) theModel
					.get(ageCountInteger);
			streamSexMapEvents(sexMap, contentMap, writer, eventFactory);
		}
	}

	static public void streamSexMapEvents(SexMap<IObservable> theModel,
			TreeMap<String, String> contentMap, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		for (int sexCount = utilitySex.getMIN_VALUE(); sexCount < theModel.size(); sexCount++) {
			Integer sexCountInteger = new Integer(sexCount);
			contentMap.put(utilitySex.getElementName(), sexCountInteger.toString());
			Integer incidenceValue = (Integer) (((WritableValue) theModel
					.get(sexCountInteger)).doGetValue());
			contentMap.put("value", incidenceValue.toString());
			streamIncidenceEvents(contentMap, writer, eventFactory);
		}
	}

	private static void streamIncidenceEvents(
			TreeMap<String, String> contentMap, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		Set<String> keySet = contentMap.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		XMLEvent event = eventFactory.createStartElement("", "", "incidence");
		writer.add(event);
		event = eventFactory.createCharacters("\n");
		writer.add(event);
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = contentMap.get(key);
			event = eventFactory.createStartElement("", "", key);
			writer.add(event);
			event = eventFactory.createCharacters(value);
			writer.add(event);
			event = eventFactory.createEndElement("", "", key);
			writer.add(event);
			event = eventFactory.createCharacters("\n");
			writer.add(event);
		}
		event = eventFactory.createEndElement("", "", "incidence");
		writer.add(event);
		event = eventFactory.createCharacters("\n");
		writer.add(event);
	}
}