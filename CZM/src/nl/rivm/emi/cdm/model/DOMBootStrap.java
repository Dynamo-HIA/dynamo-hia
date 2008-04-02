package nl.rivm.emi.cdm.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DOMBootStrap {

	private Log log = LogFactory.getLog(getClass().getName());


	public Population process2PopulationTree(File populationFile, int numberOfSteps)
			throws ParserConfigurationException, SAXException, IOException,
			CDMConfigurationException, NumberFormatException, CDMRunException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.parse(populationFile);
		// Get the reading started
		Node rootNode = document.getFirstChild();
		PopulationFactory populationFactory = new PopulationFactory("pop");
		Population population = populationFactory.makeItFromDOM(rootNode, numberOfSteps);
		if (population == null) {
			log.error("Population construction produced errors.");
		}
		return population;
	}

}
