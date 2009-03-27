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
import java.util.TreeMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StAXAgnosticTypedHashMapWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter");

	/**
	 * 
	 * @param population
	 * @param populationFile
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws IOException
	 * @throws DynamoOutputException
	 * @throws DynamoConfigurationException
	 */
	static public void produceFile(FileControlEnum fileControl,
			TypedHashMap theModel, File outputFile) throws XMLStreamException,
			UnexpectedFileStructureException, IOException,
			DynamoOutputException, DynamoConfigurationException {
		if (fileControl.isGroupEnum() || fileControl.isRootChildEnum()) {
			throw new DynamoConfigurationException(
					"FileControlEnum with first element: "
							+ fileControl.getParameterType4GroupFactory(0)
									.getXMLElementName()
							+ " has isGroupEnum "
							+ fileControl.isGroupEnum()
							+ " and isRootChildEnum "
							+ fileControl.isRootChildEnum()
							+ ", both should be false when using this writer-entrypoint.");
		}
		if (theModel != null) {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter = new FileWriter(outputFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			streamDocument(fileControl, theModel, writer, eventFactory);
			writer.flush();
			fileWriter.flush();
		} else {
			log.info("Model is null, nothing to write.");
		}
	}

	static public void streamDocument(FileControlEnum fileControl,
			TypedHashMap hierarchicalConfiguration, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException,
			DynamoOutputException {
		log.debug("Entering streamDocument.");
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		log.debug("event writer" + writer);
		log.debug("event factory" + eventFactory);
		log.debug("event fileControl" + fileControl);
		event = eventFactory.createStartElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		LeafValueMap leafValueMap = new LeafValueMap();
		flattenLeafData(fileControl, hierarchicalConfiguration, leafValueMap,
				writer, eventFactory);
		event = eventFactory.createEndElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}

	static class LeafValueMap extends LinkedHashMap<String, String> {
		public void removeLast() {
			Set<String> keySet = keySet();
			Iterator<String> iterator = keySet.iterator();
			String key = null;
			while (iterator.hasNext()) {
				key = iterator.next();
			}
			remove(key);
		}
	}

	/**
	 * Also used from StAXAgnosticGroupWriter to write-out rootchildren that
	 * have hierarchical data.
	 * 
	 * @param fileControl
	 * @param configurationLevel
	 * @param leafValueMap
	 * @param writer
	 * @param eventFactory
	 * @throws XMLStreamException
	 * @throws DynamoOutputException
	 */
	public static void flattenLeafData(FileControlEnum fileControl,
			TypedHashMap configurationLevel, LeafValueMap leafValueMap,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoOutputException {
		log.error("Recursing at level " + leafValueMap.size());
		Set<Map.Entry<Integer, Object>> entrySet = configurationLevel
				.entrySet();
		XMLTagEntity configurationLevelType = (XMLTagEntity) configurationLevel
				.getType();
		Iterator<Map.Entry<Integer, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Object> entry = iterator.next();
			String elementName = getElementName(fileControl, leafValueMap);
			leafValueMap.put(elementName,
					((AtomicTypeBase) configurationLevelType)
							.convert4File(entry.getKey()));
			log.debug("Put element: " + elementName + " at level "
					+ (leafValueMap.size() - 1) + " in the leafValueMap.");
			if (entry.getValue() instanceof HashMap) {
				flattenLeafData(fileControl, (TypedHashMap) entry.getValue(),
						leafValueMap, writer, eventFactory);
			} else {
				if (entry.getValue() instanceof ArrayList) {
					handleAggregatePayload(fileControl, leafValueMap, writer,
							eventFactory, entry);
				} else {
					// No more naked lonely single leafvalues.
					// Object containedValue = entry.getValue();
					// if (containedValue instanceof Number) {
					// Number containedNumber = (Number) containedValue;
					// elementName = getElementName(fileControl, leafValueMap);
					// leafValueMap.put(elementName, containedNumber);
					// streamEntry(fileControl, leafValueMap, containedNumber,
					// writer, eventFactory);
					// } else {
					// if (containedValue instanceof WritableValue) {
					// Object writableValueContent = ((WritableValue)
					// containedValue)
					// .doGetValue();
					// if (writableValueContent instanceof Number) {
					// streamEntry(fileControl, leafValueMap,
					// (Number) writableValueContent, writer,
					// eventFactory);
					// } else {
					// log.error("Unsupported Object type: "
					// + writableValueContent.getClass()
					// .getName());
					// }
					// } else {
					// log.error("Unsupported Object type: "
					// + containedValue.getClass().getName());
					// }
					// }
					// leafValueMap.removeLast();
					log.error("Unsupported Object type: "
							+ entry.getValue().getClass().getName());
					// Change ends.
				}
			}
		}
		leafValueMap.removeLast();
	}

	private static void handleAggregatePayload(FileControlEnum fileControl,
			LeafValueMap leafValueMap, XMLEventWriter writer,
			XMLEventFactory eventFactory, Map.Entry<Integer, Object> entry)
			throws DynamoOutputException, XMLStreamException {
		String elementName;
		ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) entry
				.getValue();
		int tupleIndex = 0;
		for (; tupleIndex < list.size(); tupleIndex++) {
			AtomicTypeObjectTuple tuple = list.get(tupleIndex);
			XMLTagEntity tupleType = tuple.getType();
			log.error("Got tuple, type \""
					+ tuple.getType().getXMLElementName() + "\"");
			Object tupleValue = tuple.getValue();
			if (tupleValue instanceof WritableValue) {
				Object containedValue = ((WritableValue) tupleValue)
						.doGetValue();
				handleAggregatePayloadValue(fileControl, leafValueMap,
						containedValue, tupleType);
			} else {
				// throw new DynamoOutputException(
				// "StAXWriter only handles WritableValue-s for aggregate payloads.");
				handleAggregatePayloadValue(fileControl, leafValueMap,
						tupleValue, tupleType);
			}
		}
		streamEntries(fileControl, leafValueMap, writer, eventFactory);
		int numberOfWrapperEntries = fileControl.getWrapperCount();
		for (; tupleIndex >= 0; tupleIndex--) {
			// String xmlElementName = ((AtomicTypeBase) fileControl
			// .getParameterType(leafValueMap.size() - 1))
			// .getXMLElementName();
			log.error("Removing leafValue \"" + (leafValueMap.get(tupleIndex))
					+ "\", size: " + leafValueMap.size() + ".");
			leafValueMap.removeLast();
		}
	}

	private static void handleAggregatePayloadValue(
			FileControlEnum fileControl, LeafValueMap leafValueMap,
			Object containedValue, XMLTagEntity containedType)
			throws DynamoOutputException {
		// String elementName;
		// if (containedValue instanceof Number) {
		// Number containedNumber = (Number) containedValue;
		// elementName = getElementName(fileControl,
		// leafValueMap);
		// leafValueMap.put(elementName, containedNumber);
		// log.error("Added \"" + elementName + "\" with value \"" +
		// containedNumber + "\", size: " + leafValueMap.size() +".");
		// } else {
		// throw new DynamoOutputException(
		// "StAXWriter only handles Number-s for aggregate payloads.");
		// }
		String elementName = containedType.getXMLElementName();
		String elementValue = ((AtomicTypeBase) containedType)
				.convert4File(containedValue);
		leafValueMap.put(elementName, elementValue);
	}

	private static String getElementName(FileControlEnum fileControl,
			LeafValueMap leafValueMap) {
		int level = leafValueMap.size();
		int numberOfWrappers = 0;
		// "Old"-type configurations do nothing with wrappers.
		if(fileControl.isGroupEnum()||fileControl.isRootChildEnum()){
			numberOfWrappers = fileControl.getWrapperCount();
		}
		AtomicTypeBase<Number> type = null;
		if (numberOfWrappers == 0) {
			type = fileControl.getParameterType(level);
		} else {
			type = (AtomicTypeBase<Number>) fileControl
					.getParameterType4GroupFactory(level + numberOfWrappers);
		}
		log.debug("getElementName() level: " + level + " numberofwrappers: " + numberOfWrappers + " type: " + type);
		String elementName = type.getXMLElementName();
		log.debug("getElementName() level: " + level + " got: " + elementName);
		return elementName;
	}

	private static void streamEntry(FileControlEnum fileControl,
			LinkedHashMap<String, Number> leafValueMap, Number containedValue,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamEntry");
		XMLEvent event;
		event = eventFactory.createStartElement("", "", fileControl
				.getRootChildElementName());
		writer.add(event);
		Iterator<Map.Entry<String, Number>> iterator = leafValueMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Number> entry = iterator.next();
			event = eventFactory.createStartElement("", "", entry.getKey());
			writer.add(event);
			Number value = entry.getValue();
			String valueString = null;
			if (value instanceof Integer) {
				valueString = ((Integer) value).toString();
			} else {
				valueString = value.toString();
			}
			event = eventFactory.createCharacters(valueString);
			writer.add(event);
			event = eventFactory.createEndElement("", "", entry.getKey());
			writer.add(event);
			log.debug("Streamed element with name: " + entry.getKey()
					+ " and valuestring: " + valueString);
		}
		int level = leafValueMap.size();
		String elementName = fileControl.getParameterType(level)
				.getXMLElementName();
		event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
		String containedValueString = null;
		if (containedValue instanceof Integer) {
			containedValueString = ((Integer) containedValue).toString();
		} else {
			containedValueString = containedValue.toString();
		}
		event = eventFactory.createCharacters(containedValue.toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		log.debug("Streamed element with name: " + elementName
				+ " and valuestring: " + containedValue.toString());
		event = eventFactory.createEndElement("", "", fileControl
				.getRootChildElementName());
		writer.add(event);
	}

	private static void streamEntries(FileControlEnum fileControl,
			LeafValueMap leafValueMap, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		log.debug("Entering streamEntriesSmarter");
		XMLEvent event;
		event = eventFactory.createStartElement("", "", fileControl
				.getRootChildElementName());
		writer.add(event);
		Iterator<Map.Entry<String, String>> iterator = leafValueMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			event = eventFactory.createStartElement("", "", entry.getKey());
			writer.add(event);
			// Number value = entry.getValue();
			// String valueString = null;
			// if (value instanceof Integer) {
			// valueString = ((Integer) value).toString();
			// } else {
			// valueString = value.toString();
			// }
			// event = eventFactory.createCharacters(valueString);
			event = eventFactory.createCharacters(entry.getValue());
			writer.add(event);
			event = eventFactory.createEndElement("", "", entry.getKey());
			writer.add(event);
			log.debug("Streamed element with name: " + entry.getKey()
					+ " and valuestring: " + entry.getValue());
		}
		event = eventFactory.createEndElement("", "", fileControl
				.getRootChildElementName());
		writer.add(event);
	}

}
