package nl.rivm.emi.cdm.population;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.DOMIndividualWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMPopulationWriter {

	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.population.PopulationWriter");

	String label = "Not initialized.";

	public DOMPopulationWriter() {
		super();
	}

	public static void writeToXMLFile(Population population, int stepNumber, File xmlFileName)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = (DocumentBuilder) dbfac
				.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		String elementName = population.getElementName();
		Element element = document.createElement(population.xmlLabelAttributeName);
		
		String label = population.getLabel();
		if (label != null && !"".equals(label)) {
			element.setAttribute("lb", label);
		}
		document.appendChild(element);
		Individual individual;
		while ((individual = population.nextIndividual()) != null) {
			DOMIndividualWriter.generateDOM(individual, stepNumber, element);
		}
		boolean isDirectory = xmlFileName.isDirectory();
		boolean canWrite = xmlFileName.canWrite();
		try {
			boolean isNew = xmlFileName.createNewFile();
			if (!isDirectory && (canWrite || isNew)) {
				Source source = new DOMSource(document);
				StreamResult result = new StreamResult(xmlFileName);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(source, result);
			}
		} catch (IOException e) {
			log.warn("File exception: " + e.getClass().getName() + " message: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}
}
