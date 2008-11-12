package nl.rivm.emi.dynamo.data.writers;

import java.util.HashMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationStAXEventConsumer;
import nl.rivm.emi.cdm.population.StAXPopulationEventStreamer;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class StAXAgnosticDocumentEventStreamer {
	Log log = LogFactory.getLog(this.getClass().getName());

	public StAXAgnosticDocumentEventStreamer() {
		super();
	}

//	public void add(Object population) {
//		this.population = (Population) population;
//	}

	/**
	 * @throws XMLStreamException
	 */
	static public void streamEvents(AtomicTypeBase<Number>[] types, HashMap hierarchicalConfiguration,
			XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		StAXAgnosticEventStreamer.streamEvents(types, hierarchicalConfiguration, writer,
				eventFactory);
		writer.flush();
		writer.close();
	}

//	public Object getPopulation() {
//		return population;
//	}
}
