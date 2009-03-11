package nl.rivm.emi.cdm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.population.Population;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;

public class StaxBootStrap {

	private Log log = LogFactory.getLog(getClass().getName());

	public Population process2PopulationTree(File populationFile,
			int numberOfSteps) throws ParserConfigurationException,
			SAXException, IOException, CDMConfigurationException,
			NumberFormatException, CDMRunException {
		Population population = null;
		try {
			if (populationFile.isFile() && populationFile.canRead()) {
				InputStream in = new FileInputStream(populationFile);
				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLStreamReaderImpl xmlr = (XMLStreamReaderImpl)factory.createXMLStreamReader(in);

				for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr
						.next()) {
					if (event == XMLStreamConstants.START_ELEMENT) {
						String element = xmlr.getLocalName();
						log.info("StAX: " + element);
					}
				}

			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Node rootNode = document.getFirstChild();
		// PopulationFactory populationFactory = new PopulationFactory("pop");
		// Population population = populationFactory.makeItFromDOM(rootNode,
		// numberOfSteps);
		// if (population == null) {
		// log.error("Population construction produced errors.");
		// }
		return population;
	}

}
