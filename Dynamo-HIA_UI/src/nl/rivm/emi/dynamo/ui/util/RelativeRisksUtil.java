package nl.rivm.emi.dynamo.ui.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class RelativeRisksUtil {
	static Log log = LogFactory.getLog("RelativeRisksUtil");

	public static Integer extractNumberOfDiseases(File configurationFile) {
		Integer numberOfDiseases = null;
		String rootElementName = null;
		try {
			// Create a builder factory
			if (configurationFile.exists() && configurationFile.canRead()) {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(false);

				// Create the builder and parse the file
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(configurationFile);
				Node rootNode = doc.getDocumentElement();
				rootElementName = rootNode.getNodeName();
				if ((rootElementName != null)
						&& ((RootElementNamesEnum.RELATIVERISKSCLUSTER
								.getNodeLabel().equals(rootElementName)))) {
					NodeList nodeList = doc
							.getElementsByTagName(XMLTagEntityEnum.TRANSITIONDESTINATION
									.getElementName());
					Integer previousToIndex = new Integer(-1);
					for (int index = 0; index < nodeList.getLength(); index++) {
						Node toNode = nodeList.item(index);
						Integer toIndex = Integer.valueOf(toNode
								.getTextContent());
						if (toIndex != null
								&& (toIndex.compareTo(previousToIndex) <= 0)) {
							numberOfDiseases = previousToIndex;
							break;
						}
						previousToIndex = toIndex;
					}
				} else {
					log.fatal("RootElementName: " + rootElementName
							+ " found in: "
							+ configurationFile.getAbsolutePath()
							+ " using DOM.");

				}
			}
			return numberOfDiseases;
		} catch (SAXException e) {
			log.fatal("Caught: " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace(System.err);
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
		return null;
	}

	public static Integer extractNumberOfCategories(File configurationFile)
			throws ConfigurationException {
		Integer numberOfCategories = null;
		String rootElementName = null;
		try {
			// Create a builder factory
			if (configurationFile.exists() && configurationFile.canRead()) {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(false);

				// Create the builder and parse the file
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(configurationFile);
				Node rootNode = doc.getDocumentElement();
				rootElementName = rootNode.getNodeName();
				if ((rootElementName != null)
						&& (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL4P
								.getNodeLabel().equals(rootElementName) || RootElementNamesEnum.RELATIVERISKS_OTHERMORT_CATEGORICAL
								.getNodeLabel().equals(rootElementName))) {
					NodeList nodeList = doc
							.getElementsByTagName(XMLTagEntityEnum.CAT
									.getElementName());
					Integer previousFromIndex = new Integer(-1);
					for (int index = 0; index < nodeList.getLength(); index++) {
						Node fromNode = nodeList.item(index);
						Integer fromIndex = Integer.valueOf(fromNode
								.getTextContent());
						if (fromIndex != null
								&& (fromIndex.compareTo(previousFromIndex) <= 0)) {
							numberOfCategories = previousFromIndex + 1;
							break;
						}
						previousFromIndex = fromIndex;
					}
				} else {
					log.fatal("RootElementName: " + rootElementName
							+ " found in: "
							+ configurationFile.getAbsolutePath()
							+ " using DOM.");
					throw new ConfigurationException(
							"extractNumberOfCategories() called for wrong rootelement: "
									+ rootElementName);
				}
			}
			return numberOfCategories;
		} catch (SAXException e) {
			log.fatal("Caught: " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace(System.err);
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
		return null;
	}

	static public class localSAXErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			throw exception;
		}

	}
}
