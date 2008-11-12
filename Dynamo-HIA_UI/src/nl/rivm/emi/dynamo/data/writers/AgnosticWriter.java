package nl.rivm.emi.dynamo.data.writers;

/**
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.stax.StAXPopulationDocumentEventStreamer;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class AgnosticWriter {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.writers.AgnosticWriter");


	/**
	 * 
	 * @param population
	 * @param populationFile
	 * @throws XMLStreamException
	 * @throws UnexpectedFileStructureException
	 * @throws IOException
	 */
		static public void produceFile(AtomicTypeBase<Number>[] types, HashMap hierarchicalConfiguration, File outputFile)
				throws XMLStreamException, UnexpectedFileStructureException,
				IOException {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			Writer fileWriter = new FileWriter(outputFile);
			XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
			StAXAgnosticDocumentEventStreamer agnosticStreamer = new StAXAgnosticDocumentEventStreamer();
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			agnosticStreamer.streamEvents( types,hierarchicalConfiguration, writer, eventFactory);
		}
	/**
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 */

	public static void logContent(AtomicTypeBase<Number>[] types, HashMap theObject)
			throws ConfigurationException {
		log.debug("Starting log.");
		int currentLevel = 0;
		logALevel(types, theObject, currentLevel);
	}

	private static void logALevel(AtomicTypeBase<Number>[] types, Object theObject, int currentLevel) {
		StringBuffer tabs = new StringBuffer();
		for (int count=0;count<currentLevel; count++){
			tabs.append("\t");
		}
		if (theObject instanceof HashMap) {
			Iterator iterator = ((HashMap) theObject).keySet().iterator();
			while (iterator.hasNext()) {
				Integer key = (Integer) iterator.next();
				log.debug( tabs.toString() + " hashmap key " + key + " type " + types[currentLevel].getElementName());
				Object nextLevel = ((HashMap) theObject).get(key);
				logALevel(types, nextLevel, currentLevel + 1);
			}
		} else {
			log.debug( tabs.toString() + " leaf value " + ((Number)theObject).toString()+ " type " + types[currentLevel].getElementName());
		}
	}
}
