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
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class StAXTransitionDriftNettoWriter {
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
	 * @throws nl.rivm.emi.cdm.exceptions.DynamoConfigurationException 
	 */
	static public void produceFile(FileControlEnum fileControl,
			TransitionDriftNettoObject theModel, File outputFile) throws XMLStreamException,
			UnexpectedFileStructureException, IOException,
			DynamoOutputException, DynamoConfigurationException, nl.rivm.emi.cdm.exceptions.DynamoConfigurationException {
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
			TransitionDriftNettoObject hierarchicalConfiguration, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException,
			DynamoOutputException, nl.rivm.emi.cdm.exceptions.DynamoConfigurationException {
		log.debug("Entering streamDocument.");
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		log.debug("event writer" + writer);
		log.debug("event factory" + eventFactory);
		log.debug("event fileControl" + fileControl);
		event = eventFactory.createStartElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		writeDatum(fileControl, hierarchicalConfiguration, writer, eventFactory);
		event = eventFactory.createEndElement("", "", fileControl
				.getRootElementName());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
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
	 * @throws nl.rivm.emi.cdm.exceptions.DynamoConfigurationException 
	 */
	public static void writeDatum(FileControlEnum fileControl,
			TransitionDriftNettoObject configurationLevel, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, DynamoOutputException, nl.rivm.emi.cdm.exceptions.DynamoConfigurationException {
			XMLTagEntity tupleType = XMLTagEntityEnum.TREND.getTheType();
			log.error("Got tuple, type \""
					+ tupleType.getXMLElementName() + "\"");
			Object tupleValue = configurationLevel.getObservableTrend();
			if (tupleValue instanceof WritableValue) {
				Object containedValue = ((WritableValue) tupleValue)
						.doGetValue();
				XMLEvent event = eventFactory.createStartElement("", "", tupleType.getXMLElementName());
				writer.add(event);
				event = eventFactory.createCharacters(((AbstractValue)tupleType).convert4File(containedValue));
				writer.add(event);
				event = eventFactory.createEndElement("", "", tupleType.getXMLElementName());
				writer.add(event);
			}
			}

}
