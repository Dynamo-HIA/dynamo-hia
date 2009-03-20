package nl.rivm.emi.dynamo.data.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StAXAgnosticWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter");

	/**
	 * 
	 * @param population
	 * @param populationFile
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws IOException
	 * @throws DynamoConfigurationException
	 */
	static public void produceFile(FileControlEnum fileControl,
			HashMap<Integer, Object> theModel, File outputFile)
			throws XMLStreamException, UnexpectedFileStructureException,
			IOException, DynamoConfigurationException {
		log.debug("Entering produceFile");
		if (theModel != null) {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter;
			fileWriter = new FileWriter(outputFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			streamEvents(fileControl, theModel, writer, eventFactory);
			writer.flush();
		} else {
			log.info("Model is null, nothing to write.");
		}
	}

	static public void streamEvents(FileControlEnum fileControl,
			HashMap<Integer, Object> theModel, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException,
			DynamoConfigurationException {
		log.debug("Entering streamEvents.");
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		event = eventFactory.createStartElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		LinkedHashMap<String, Number> containerValuesMap = new LinkedHashMap<String, Number>();
		recurseLeafData(fileControl, theModel, containerValuesMap, writer,
				eventFactory);
		event = eventFactory.createEndElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}

	private static void recurseLeafData(FileControlEnum fileControl,
			HashMap<Integer, Object> theModel,
			LinkedHashMap<String, Number> containerValuesMap,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoConfigurationException {
		log.debug("Recursing at level " + containerValuesMap.size());
		Set<Map.Entry<Integer, Object>> entrySet = theModel.entrySet();
		Iterator<Map.Entry<Integer, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Object> entry = iterator.next();
			int level = containerValuesMap.size();
			AtomicTypeBase<Number> type = fileControl.getParameterType(level);
			String elementName = type.getXMLElementName();
			containerValuesMap.put(elementName, entry.getKey());
			if (entry.getValue() instanceof HashMap) {
				recurseLeafData(fileControl, (HashMap<Integer, Object>) entry
						.getValue(), containerValuesMap, writer, eventFactory);
			} else {
				Object containedObject = entry.getValue();
				streamRootChildStartPlusContainerEntries(fileControl,
						containerValuesMap, writer, eventFactory);
				int containedLevel = containerValuesMap.size();
				handleContainedObject(fileControl, containedLevel, writer,
						eventFactory, containedObject);
				streamRootChildEnd(fileControl, writer, eventFactory);
			}
			containerValuesMap.remove(elementName);
		}
	}

	private static int handleContainedObject(FileControlEnum fileControl,
			int level, XMLEventWriter writer, XMLEventFactory eventFactory,
			Object containedObject) throws XMLStreamException,
			DynamoConfigurationException {
		if (containedObject instanceof Number) {
			Number containedNumber = (Number) containedObject;
			level = streamEntry(containedNumber, fileControl, level, writer,
					eventFactory);
		} else {
			if (containedObject instanceof String) {
				String containedString = (String) containedObject;
				level = streamEntry(containedString, fileControl, level,
						writer, eventFactory);
			} else {
				if (containedObject instanceof ArrayList) {
					for (Object entry : (ArrayList) containedObject) {
						AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) entry;
						Object containedPayloadObject = tuple.getValue();
						level = handleContainedObject(fileControl, level,
								writer, eventFactory, containedPayloadObject);
					}
				} else {
					if (containedObject instanceof WritableValue) {
						Object writableValueContent = ((WritableValue) containedObject)
								.doGetValue();
						level = handleContainedObject(fileControl, level,
								writer, eventFactory, writableValueContent);
					} else {
						throw new DynamoConfigurationException(
								"Unsupported Object type: "
										+ containedObject.getClass().getName());
					}
				}
			}
		}
		return level;
	}

	private static void streamRootChildStartPlusContainerEntries(
			FileControlEnum fileControl,
			LinkedHashMap<String, Number> containerValuesMap,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamContainerEntries.");
		XMLEvent event;
		event = eventFactory.createStartElement("", "",
				fileControl.rootChildElementName);
		writer.add(event);
		Iterator<Map.Entry<String, Number>> iterator = containerValuesMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Number> entry = iterator.next();
			event = eventFactory.createStartElement("", "", entry.getKey());
			writer.add(event);
			event = eventFactory.createCharacters(entry.getValue().toString());
			writer.add(event);
			event = eventFactory.createEndElement("", "", entry.getKey());
			writer.add(event);
		}
	}

	private static void streamRootChildEnd(FileControlEnum fileControl,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamRootChildEnd.");
		XMLEvent event;
		event = eventFactory.createEndElement("", "",
				fileControl.rootChildElementName);
		writer.add(event);
	}

	private static int streamEntry(Number containedValue,
			FileControlEnum fileControl, int level, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		log.debug("Entering streamEntry.");
		XMLEvent event;
		String elementName = fileControl.getParameterType(level)
				.getXMLElementName();
		event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
		event = eventFactory.createCharacters(containedValue.toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		level++;
		return level;
	}

	private static int streamEntry(String containedValue,
			FileControlEnum fileControl, int level, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		log.debug("Entering streamEntry.");
		XMLEvent event;
		String elementName = fileControl.getParameterType(level)
				.getXMLElementName();
		event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
		event = eventFactory.createCharacters(containedValue.toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		level++;
		return level;
	}
}
