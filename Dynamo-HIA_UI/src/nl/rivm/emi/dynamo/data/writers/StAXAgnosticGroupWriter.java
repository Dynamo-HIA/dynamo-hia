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
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StAXAgnosticGroupWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter");

	/**
	 * 
	 * @param rootElementName
	 *            TODO
	 * @param population
	 * @param populationFile
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws IOException
	 * @throws DynamoConfigurationException
	 */
	static public void produceFile(String rootElementName,
			HashMap<String, Object> theModel, File outputFile)
			throws XMLStreamException, UnexpectedFileStructureException,
			IOException, DynamoConfigurationException {
		log.debug("Entering produceFile");
		if (theModel != null) { // We're in business.
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter;
			fileWriter = new FileWriter(outputFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			XMLEvent event = eventFactory.createStartDocument();
			writer.add(event);
			event = eventFactory.createStartElement("", "", rootElementName);
			writer.add(event);
			streamRootChildren(theModel, writer, eventFactory);
			event = eventFactory.createEndElement("", "", rootElementName);
			writer.add(event);
			event = eventFactory.createEndDocument();
			writer.add(event);
			writer.flush();
		} else {
			log.info("Model is null, nothing to write.");
		}
	}

	static public void streamRootChildren(HashMap<String, Object> theModel,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoConfigurationException {
		log.debug("Entering streamEvents.");
		Iterator<String> rootChildNameIterator = theModel.keySet().iterator();
		while (rootChildNameIterator.hasNext()) {
			String rootChildElementName = rootChildNameIterator.next();
			Object rootChildObject = theModel.get(rootChildElementName);
			log.fatal("RootChildName: " + rootChildElementName);
			streamRootChildStart(writer, eventFactory, rootChildElementName);
			if (rootChildObject instanceof TypedHashMap<?>) {
				FileControlEnum myEnum = FileControlSingleton.getInstance().get(rootChildElementName);
				String wrapperElementName = "bogus";
				XMLTagEntity rootChildEntity = XMLTagEntitySingleton.getInstance().get(rootChildElementName);
				if(rootChildEntity instanceof WrapperType){
					WrapperType nextType = ((WrapperType)rootChildEntity).getNextWrapper();
				wrapperElementName =((XMLTagEntity)nextType).getXMLElementName();
				}
				 LinkedHashMap<String, Number> containerValuesMap = new
				 LinkedHashMap<String, Number>();
				 recurseLeafData((TypedHashMap<?>)rootChildObject, wrapperElementName,
				 myEnum, containerValuesMap, writer, eventFactory);
			} else {
				if (rootChildObject instanceof AtomicTypeObjectTuple) {
					AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) rootChildObject;
					XMLTagEntity theType = tuple.getType();
					Object theValue = tuple.getValue();
					String valueAsString = ((AtomicTypeBase)theType).convert4View(theValue);
				XMLEvent event = eventFactory.createCharacters(valueAsString);
					writer.add(event);
				}
			}
			streamRootChildEnd(writer, eventFactory, rootChildElementName);
		}
	}

	private static void recurseLeafData(TypedHashMap<?> containerLevel,
			String wrapperElementName,
			FileControlEnum fileControl, LinkedHashMap<String, Number> containerValuesMap, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoConfigurationException {
		log.debug("Recursing at level " + containerValuesMap.size());
		Set<Map.Entry<Integer, Object>> entrySet = containerLevel.entrySet();
		Iterator<Map.Entry<Integer, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Object> entry = iterator.next();
			int level = containerValuesMap.size();
			AtomicTypeBase<Number> type = fileControl.getParameterType(level);
			String elementName = type.getXMLElementName();
			containerValuesMap.put(elementName, entry.getKey());
			if (entry.getValue() instanceof HashMap) {
				recurseLeafData((TypedHashMap<?>) entry
						.getValue(), wrapperElementName, fileControl, containerValuesMap, writer, eventFactory);
			} else {
				Object containedObject = entry.getValue();
				streamWrapperStartPlusContainerEntries(containerValuesMap, wrapperElementName, writer, eventFactory);
				int containedLevel = containerValuesMap.size();
				handleContainedObject( fileControl , containedLevel,
						writer, eventFactory, containedObject);
				streamWrapperEnd(wrapperElementName, writer, eventFactory);
			}
			containerValuesMap.remove(elementName);
		}
	}

	private static int handleContainedObject(
			FileControlEnum fileControl, int level, XMLEventWriter writer,
			XMLEventFactory eventFactory, Object containedObject) throws XMLStreamException,
			DynamoConfigurationException {
		if (containedObject instanceof Number) {
			Number containedNumber = (Number) containedObject;
			level = streamEntry(containedNumber, level, writer,
					eventFactory, fileControl);
		} else {
			if (containedObject instanceof String) {
				String containedString = (String) containedObject;
				level = streamEntry(containedString, level,
						writer, eventFactory, fileControl);
			} else {
				if (containedObject instanceof ArrayList) {
					for (Object entry : (ArrayList) containedObject) {
						AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) entry;
						Object containedPayloadObject = tuple.getValue();
						level = handleContainedObject(fileControl,
								level, writer, eventFactory, containedPayloadObject);
					}
				} else {
					if (containedObject instanceof WritableValue) {
						Object writableValueContent = ((WritableValue) containedObject)
								.doGetValue();
						level = handleContainedObject(fileControl,
								level, writer, eventFactory, writableValueContent);
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

	private static void streamRootChildStart(XMLEventWriter writer,
			XMLEventFactory eventFactory, String rootChildElementName)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "",
				rootChildElementName);
		writer.add(event);
	}

	private static void streamRootChildEnd(XMLEventWriter writer,
			XMLEventFactory eventFactory, String rootChildElementName)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createEndElement("", "", rootChildElementName);
		writer.add(event);
	}

	private static void streamWrapperStartPlusContainerEntries(
			LinkedHashMap<String, Number> containerValuesMap,
			String wrapperElementName, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamContainerEntries.");
		XMLEvent event;
		event = eventFactory.createStartElement("", "",
				wrapperElementName);
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

	private static void streamWrapperEnd(String wrapperElementName, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamRootChildEnd.");
		XMLEvent event;
		event = eventFactory.createEndElement("", "",
				wrapperElementName);
		writer.add(event);
	}

	private static int streamEntry(Number containedValue,
			int level, XMLEventWriter writer,
			XMLEventFactory eventFactory, FileControlEnum fileControl) throws XMLStreamException {
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
			int level, XMLEventWriter writer,
			XMLEventFactory eventFactory, FileControlEnum fileControl) throws XMLStreamException {
		log.debug("Entering streamEntry.");
		XMLEvent event;
		String elementName = fileControl.getParameterType_MK2(level + 1)
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