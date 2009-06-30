package nl.rivm.emi.dynamo.data.writers;

/**
 * 20090330 mondeelr Changes to allow String type keys in TypedHashMap-s.
 */
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
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter.LeafValueMap;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

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
	 * @throws DynamoOutputException
	 */
	static public void produceFile(String rootElementName,
			HashMap<String, Object> theModel, File outputFile)
			throws XMLStreamException, UnexpectedFileStructureException,
			IOException, DynamoConfigurationException, DynamoOutputException {
		log.fatal("Entering produceFile");
		log.fatal("theModel" + theModel);
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
			log
					.debug("Model is null, writing an empty file for rootElementName: "
							+ rootElementName + ".");
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter;
			fileWriter = new FileWriter(outputFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			XMLEvent event = eventFactory.createStartDocument();
			writer.add(event);
			event = eventFactory.createStartElement("", "", rootElementName);
			writer.add(event);
			event = eventFactory.createEndElement("", "", rootElementName);
			writer.add(event);
			event = eventFactory.createEndDocument();
			writer.add(event);
			writer.flush();
		}
	}

	static public void streamRootChildren(HashMap<String, Object> theModel,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoConfigurationException,
			DynamoOutputException {
		log.debug("Entering streamRootChildren.");
		Iterator<String> rootChildNameIterator = theModel.keySet().iterator();
		while (rootChildNameIterator.hasNext()) {
			String rootChildElementName = rootChildNameIterator.next();
			Object rootChildObject = theModel.get(rootChildElementName);
			log.fatal("RootChildName: " + rootChildElementName);
			streamRootChildStart(writer, eventFactory, rootChildElementName);
			if (rootChildObject instanceof TypedHashMap<?>) {
				// handleHierarchicRootChildData(writer, eventFactory,
				// rootChildElementName, rootChildObject);

				LeafValueMap leafValueMap = new LeafValueMap();
				FileControlEnum myEnum = FileControlSingleton.getInstance()
						.get(rootChildElementName);
				StAXAgnosticTypedHashMapWriter.flattenLeafData(myEnum,
						(TypedHashMap) rootChildObject, leafValueMap, writer,
						eventFactory);

			} else {
				handleSolitaryRootChildData(writer, eventFactory,
						rootChildElementName, rootChildObject);
			}
			streamRootChildEnd(writer, eventFactory, rootChildElementName);
		}
	}

	private static void handleSolitaryRootChildData(XMLEventWriter writer,
			XMLEventFactory eventFactory, String rootChildElementName,
			Object rootChildObject) throws XMLStreamException {
		Object object2Convert = rootChildObject;
		if (rootChildObject instanceof AtomicTypeObjectTuple) {
			object2Convert = ((AtomicTypeObjectTuple) rootChildObject)
					.getValue();
		}
		// Some extra handling to handle empty RootChildren ("cutoffs" more specifically).
		XMLTagEntity myTagEntity = XMLTagEntitySingleton.getInstance().get(
				rootChildElementName);
		AtomicTypeBase<?> myType = null;
		if (myTagEntity != null) {
			if (myTagEntity instanceof AtomicTypeBase) {
				myType = (AtomicTypeBase<?>) myTagEntity;
				String valueAsString = myType.convert4File(object2Convert);
				XMLEvent event = eventFactory.createCharacters(valueAsString);
				writer.add(event);
			}
		} else {
			log.fatal("No type found with name " + rootChildElementName);
			XMLEvent event = eventFactory
					.createCharacters("Fatality, see logfile");
			writer.add(event);
		}
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
		XMLEvent event = eventFactory.createEndElement("", "",
				rootChildElementName);
		writer.add(event);
	}

	private static void handleHierarchicRootChildData(XMLEventWriter writer,
			XMLEventFactory eventFactory, String rootChildElementName,
			Object rootChildObject) throws XMLStreamException,
			DynamoConfigurationException {
		FileControlEnum myEnum = FileControlSingleton.getInstance().get(
				rootChildElementName);
		int numberOfWrappers = 0;
		String wrapperElementName = "bogus";
		XMLTagEntity rootChildEntity = XMLTagEntitySingleton.getInstance().get(
				rootChildElementName);
		if (rootChildEntity instanceof WrapperType) {
			numberOfWrappers++;
			WrapperType nextType = ((WrapperType) rootChildEntity)
					.getNextWrapper();
			if (nextType != null) {
				numberOfWrappers++;
				wrapperElementName = ((XMLTagEntity) nextType)
						.getXMLElementName();
			} else {
				wrapperElementName = rootChildEntity.getXMLElementName();
			}
		}
		LinkedHashMap<String, Object> containerValuesMap = new LinkedHashMap<String, Object>();
		recurseLeafData((TypedHashMap<?>) rootChildObject, wrapperElementName,
				numberOfWrappers, myEnum, containerValuesMap, writer,
				eventFactory);
	}

	private static void recurseLeafData(TypedHashMap<?> containerLevel,
			String wrapperElementName, int numberOfWrappers,
			FileControlEnum fileControl,
			LinkedHashMap<String, Object> containerValuesMap,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoConfigurationException {
		log.debug("Recursing at level " + containerValuesMap.size());
		Set<Map.Entry<Object, Object>> entrySet = containerLevel.entrySet();
		Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();
			int level = numberOfWrappers + containerValuesMap.size();
			/* AtomicTypeBase<Number> */XMLTagEntity type = fileControl
					.getParameterType4GroupFactory(level);
			String elementName = type.getXMLElementName();
			containerValuesMap.put(elementName, entry.getKey());
			if (entry.getValue() instanceof HashMap) {
				recurseLeafData((TypedHashMap<?>) entry.getValue(),
						wrapperElementName, numberOfWrappers, fileControl,
						containerValuesMap, writer, eventFactory);
			} else {
				Object containedObject = entry.getValue();
				streamWrapperStartPlusContainerEntries(containerValuesMap,
						wrapperElementName, writer, eventFactory);
				// int containedLevel = containerValuesMap.size();
				int containedLevel = containerValuesMap.size() + 1;
				handleContainedObject(fileControl, containedLevel, writer,
						eventFactory, containedObject);
				streamWrapperEnd(wrapperElementName, writer, eventFactory);
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
			level = streamEntry(containedNumber, level, writer, eventFactory,
					fileControl);
		} else {
			if (containedObject instanceof String) {
				String containedString = (String) containedObject;
				level = streamEntry(containedString, level, writer,
						eventFactory, fileControl);
			} else {
				if (containedObject instanceof ArrayList) {
					for (Object entry : (ArrayList<?>) containedObject) {
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

	private static void streamWrapperStartPlusContainerEntries(
			LinkedHashMap<String, Object> containerValuesMap,
			String wrapperElementName, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		log.debug("Entering streamContainerEntries.");
		XMLEvent event;
		event = eventFactory.createStartElement("", "", wrapperElementName);
		writer.add(event);
		Iterator<Map.Entry<String, Object>> iterator = containerValuesMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			event = eventFactory.createStartElement("", "", entry.getKey());
			writer.add(event);
			event = eventFactory.createCharacters(entry.getValue().toString());
			writer.add(event);
			event = eventFactory.createEndElement("", "", entry.getKey());
			writer.add(event);
		}
	}

	private static void streamWrapperEnd(String wrapperElementName,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		log.debug("Entering streamRootChildEnd.");
		XMLEvent event;
		event = eventFactory.createEndElement("", "", wrapperElementName);
		writer.add(event);
	}

	private static int streamEntry(Number containedValue, int level,
			XMLEventWriter writer, XMLEventFactory eventFactory,
			FileControlEnum fileControl) throws XMLStreamException {
		XMLEvent event;
		XMLTagEntity parameterType = fileControl
				.getParameterType4GroupFactory(level);
		String elementName = parameterType.getXMLElementName();
		log.debug("Entering streamEntry for Number and elementname: "
				+ elementName + ".");
		event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
		event = eventFactory.createCharacters(containedValue.toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		level++;
		return level;
	}

	private static int streamEntry(String containedValue, int level,
			XMLEventWriter writer, XMLEventFactory eventFactory,
			FileControlEnum fileControl) throws XMLStreamException {
		level++;
		XMLEvent event;
		XMLTagEntity parameterType = fileControl
				.getParameterType4GroupFactory(level);
		String elementName = parameterType.getXMLElementName();
		log.debug("Entering streamEntry for String and elementname: "
				+ elementName + ".");
		event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
		event = eventFactory.createCharacters(containedValue.toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		return level;
	}
}
