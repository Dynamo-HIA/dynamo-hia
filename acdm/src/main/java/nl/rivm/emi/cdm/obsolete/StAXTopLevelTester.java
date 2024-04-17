package nl.rivm.emi.cdm.obsolete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

public class StAXTopLevelTester {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.staxStAXTopLevelReader");

	static public Object processFile(File populationFile)
			throws CDMConfigurationException {
		Object whatEver = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		Reader fileReader;
		try {
			fileReader = new FileReader(populationFile);
			XMLEventReader reader;
			reader = factory.createXMLEventReader(fileReader);

			NopStAXEventConsumerBase baseFactory = new NopStAXEventConsumerBase();
			while (reader.hasNext()) {
				baseFactory.processMyEvents(reader);
			}
			return whatEver;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CDMConfigurationException("");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CDMConfigurationException("");
		}
	}

}
