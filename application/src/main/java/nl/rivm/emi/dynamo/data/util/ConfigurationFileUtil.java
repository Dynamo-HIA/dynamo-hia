package nl.rivm.emi.dynamo.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr<br/>
 *         The name says it all.<br/>
 *         The utility methods are all geared to getting specific parts of the
 *         configuration file without a fuss.
 */
public class ConfigurationFileUtil {

	private static Log log = LogFactory.getLog(ConfigurationFileUtil.class);

	/**
	 * Does what the methodname says.
	 * 
	 * @param selectedNode
	 *            Node where the navigation inside the method must start.
	 * @return The rootelement that has been extracted.
	 * @throws TreeStructureException
	 * @throws DynamoConfigurationException
	 */
	static public String extractRootElementNameFromSiblingConfiguration(
			BaseNode selectedNode) throws TreeStructureException,
			DynamoConfigurationException {
		String rootElementName = null;
		if (selectedNode instanceof ChildNode) {
			BaseNode parent = (BaseNode) ((ChildNode) selectedNode).getParent();
			rootElementName = extractRootElementNameFromChildConfiguration(parent);
		}
		return rootElementName;
	}

	/**
	 * Does what the methodname says. Wraps the method below and suppresses the
	 * Exeption thrown, returns null instead.
	 * 
	 * @param selectedNode
	 *            Node where the navigation inside the method must start.
	 * @return The rootelement that has been extracted. null when none was
	 *         found.
	 */
	@SuppressWarnings("finally")
	static public String exceptionFreeExtractRootElementNameFromChildConfiguration(
			BaseNode selectedNode) {
		String resultString = null;
		try {
			resultString = extractRootElementNameFromChildConfiguration(selectedNode);
		} catch (Exception e) {
			// Provide at least some feedback.
			log.error("Exception " + e.getClass().getName()
					+ " caught with message: " + e.getMessage());
			e.printStackTrace();
		} finally {
			return resultString;
		}
	}

	/**
	 * Does what the methodname says.
	 * 
	 * @param selectedNode
	 *            Node where the navigation inside the method must start.
	 * @return The rootelement that has been extracted.
	 * @throws TreeStructureException
	 *             Thrown when the structure of the tree was not as expected.
	 * @throws DynamoConfigurationException
	 *             Thrown when the structure of the XML configuration file was
	 *             not as expected.
	 */
	static public String extractRootElementNameFromChildConfiguration(
			BaseNode selectedNode) throws TreeStructureException,
			DynamoConfigurationException {
		String rootElementName = null;
		if (selectedNode instanceof ParentNode) {
			Object[] children = ((ParentNode) selectedNode).getChildren();
			for (Object childNode : children) {
				if (childNode instanceof BaseNode) {
					if (((BaseNode) childNode).deriveNodeLabel()
							.equalsIgnoreCase("configuration")) {
						if (childNode instanceof FileNode) {
							rootElementName = extractRootElementNameIncludingSchemaCheck(((BaseNode) childNode)
									.getPhysicalStorage());
						} else {
							throw new TreeStructureException(
									"extractRootElementNameFromChildConfiguration works only on FileNode children.");
						}
					}
				} else {
					throw new TreeStructureException(
							"extractRootElementNameFromChildConfiguration works only on BaseNode children.");

				}
			}
		} else {
			throw new TreeStructureException(
					"extractRootElementNameFromChildConfiguration works only on a ParentNode selection.");
		}
		return rootElementName;
	}

	/**
	 * Does what the methodname says.
	 * 
	 * @param configurationFile
	 *            The File the rootelementname has to be found in.
	 * @return The rootelement that has been extracted.
	 * @throws DynamoConfigurationException
	 *             Thrown when the structure of the XML configuration file was
	 *             not as expected.
	 */
	static public String justExtractRootElementName(File configurationFile)
			throws DynamoConfigurationException {
		String rootElementName = null;
		// try {
		if (configurationFile.exists()) {
			if (configurationFile.isFile()) {
				if (configurationFile.canRead()) {
					// 20090629 Switched to STax to improve performance.
					// XMLConfiguration configurationFromFile;
					// configurationFromFile = new XMLConfiguration(
					// configurationFile);
					// rootElementName = configurationFromFile
					// .getRootElementName();
					rootElementName = getRootElementNameUsingSTax(configurationFile
							.getAbsolutePath());
				}
			}
		}
		return rootElementName;
		// } catch (ConfigurationException e) {
		// // Exception is not thrown again
		// // because the application has to continue
		// ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e,
		// configurationFile.getAbsolutePath());
		// return rootElementName;
		// }
	}

	/**
	 * Does what the methodname says.
	 * 
	 * @param fileName
	 *            The name of the File the rootelementname has to be found in.
	 * @return The rootelement that has been extracted.
	 */
	static String getRootElementNameUsingSTax(String filename) {
		String rootElementName = null;
		FileInputStream fIStream = null;
		XMLEventReader r = null;
		try {
			fIStream = new FileInputStream(filename);
			int fileSize = fIStream.available();
			// The STAX-parser has a bug, after hasNext() is true next() blows
			// up on an empty file.
			if (fileSize != 0) {
				XMLInputFactory factory = XMLInputFactory.newInstance();
				r = factory.createXMLEventReader(filename, fIStream);
				boolean firstStartElement = false;
				String firstStartElementName = null;
				while (r.hasNext() && !firstStartElement) {
					XMLEvent event = r.nextEvent();
					int eventType = event.getEventType();
					switch (eventType) {
					case XMLEvent.START_ELEMENT:
						firstStartElement = true;
						StartElement startElement = event.asStartElement();
						QName elementQName = startElement.getName();
						firstStartElementName = elementQName.getLocalPart();
						log.debug("STax-event: START_ELEMENT, local namepart: "
								+ firstStartElementName);
						break;
					case XMLEvent.END_ELEMENT:
						log.debug("STax-event: END_ELEMENT");
						break;
					case XMLEvent.PROCESSING_INSTRUCTION:
						log.debug("STax-event: PROCESSING_INSTRUCTION");
						break;
					case XMLEvent.CHARACTERS:
						log.debug("STax-event: CHARACTERS");
						break;
					case XMLEvent.COMMENT:
						log.debug("STax-event: COMMENT");
						break;
					case XMLEvent.START_DOCUMENT:
						log.debug("STax-event: START_DOCUMENT");
						break;
					case XMLEvent.END_DOCUMENT:
						log.debug("STax-event: END_DOCUMENT");
						break;
					case XMLEvent.ENTITY_REFERENCE:
						log.debug("STax-event: ENTITY_REFERENCE");
						break;
					case XMLEvent.ATTRIBUTE:
						log.debug("STax-event: ATTRIBUTE");
						break;
					case XMLEvent.DTD:
						log.debug("STax-event: DTD");
						break;
					case XMLEvent.CDATA:
						log.debug("STax-event: CDATA");
						break;
					case XMLEvent.SPACE:
						log.debug("STax-event: SPACE");
						break;
					default:
						log.error("STax-event: UNKNOWN_EVENT_TYPE " + ","
								+ eventType);
					}
				}
				rootElementName = firstStartElementName;
				if (r != null) {
					r.close();
				}
			} else {
				log.fatal("Empty XML-file in tree: " + filename);
			}
			if (fIStream != null) {
				fIStream.close();
			}
			return rootElementName;
		} catch (FileNotFoundException e1) {
			log.error(e1.getClass().getName() + " " + e1.getMessage());
			e1.printStackTrace();
			return rootElementName;
		} catch (XMLStreamException e1) {
			log.error(e1.getClass().getName() + " " + e1.getMessage());
			e1.printStackTrace();
			return rootElementName;
		} catch (IOException e) {
			log.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			return rootElementName;
		}
	}

	/**
	 * Does what the methodname says.
	 * 
	 * @param configurationFile
	 *            The File the rootelementname has to be extracted from.
	 * @return The rootelement that has been extracted.
	 * @throws DynamoConfigurationException
	 *             Thrown when the structure of the XML configuration file was
	 *             not as expected.
	 */
	static public String extractRootElementNameIncludingSchemaCheck(
			File configurationFile) throws DynamoConfigurationException {
		String rootElementName = null;
		try {
			if (configurationFile.exists()) {
				if (configurationFile.isFile()) {
					if (configurationFile.canRead()) {
						XMLConfiguration configurationFromFile;
						configurationFromFile = new XMLConfiguration(
								configurationFile);

						// Validate the xml by xsd schema
						// WORKAROUND: clear() is put after the constructor
						// (also calls load()).
						// The config cannot be loaded twice,
						// because the contents will be doubled.
						configurationFromFile.clear();

						// Validate the xml by xsd schema
						configurationFromFile.setValidating(true);
						configurationFromFile.load();

						rootElementName = configurationFromFile
								.getRootElementName();
					}
				}
			}
			return rootElementName;
		} catch (ConfigurationException e) {
			// Exception is not thrown again
			// because the application has to continue
			String exceptionMessage = e.getMessage();
			if ((exceptionMessage != null)
					&& exceptionMessage
							.contains("Unable to load the configuration")) {
				handleErrorMessage(log, e.getMessage(), e, configurationFile
						.getAbsolutePath());
				return rootElementName;
			} else {
				ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e,
						configurationFile.getAbsolutePath());
				return rootElementName;
			}
		}
	}

	/**
	 * 
	 * Handles the error messages provided, in case the root cause has to be
	 * shown
	 * 
	 * Cloned and adapted from: ErrorMessageUtil.handleErrorMessage(...)
	 * 
	 * @param log
	 * @param cdmErrorMessage
	 * @param e
	 * @param fileName
	 * @throws DynamoConfigurationException
	 */
	public static void handleErrorMessage(Log log, String cdmErrorMessage,
			Exception e, String fileName) throws DynamoConfigurationException {
		// e.printStackTrace();
		// Show the error message and the nested cause of the error
		String errorMessage = "";
		if (e.getCause() != null) {
			errorMessage = "Could not read the data from file:\n\n"
					+ fileName
					+ "\n\nbecause the format contains one or more errors."
					+ " Below is a technical message that contains the specifics.\n\n";

			if (!e.getCause().getMessage().contains(":")) {
				errorMessage += "XML-schema validation error: "
						+ e.getCause().getMessage();
			} else {
				errorMessage += "XML-schema  validation error: ";
				String[] splits = e.getCause().getMessage().split(":");
				for (int i = 1; i < splits.length; i++) {
					errorMessage += splits[i];
				}
			}
			// errorMessage += " related to file: " + fileName;
		} else {
			errorMessage = cdmErrorMessage;
		}
		log.error(errorMessage);
		throw new DynamoConfigurationException(errorMessage);
	}

	/**
	 * Returns the number of classes the riskfactor has been configured for. The
	 * method is meant be called on the configuration of categorical or compound
	 * riskfactors.
	 * 
	 * @param configurationFile
	 *            The File the number of classes must be found in.
	 * @return The number of classes found. Null when no classes have been
	 *         found.
	 * @throws DynamoConfigurationException
	 *             Thrown when the filestructure was unexpected.
	 */
	public static Integer extractNumberOfClasses(File configurationFile)
			throws DynamoConfigurationException {
		Integer numberOfCategories = null;
		try {
			String rootElementName = extractRootElementNameIncludingSchemaCheck(configurationFile);
			if (rootElementName != null) {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName)
						|| RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(rootElementName)) {
					XMLConfiguration configurationFromFile = new XMLConfiguration(
							configurationFile);

					// Validate the xml by xsd schema
					// WORKAROUND: clear() is put after the constructor (also
					// calls load()).
					// The config cannot be loaded twice,
					// because the contents will be doubled.
					configurationFromFile.clear();

					// Validate the xml by xsd schema
					configurationFromFile.setValidating(true);
					configurationFromFile.load();

					ConfigurationNode rootNode = configurationFromFile
							.getRootNode();
					ConfigurationNode firstRootChild = rootNode.getChild(0);
					numberOfCategories = firstRootChild.getChildrenCount();
				}
			}
			return numberOfCategories;
		} catch (ConfigurationException e) {
			// Exception is not thrown again
			// because the application has to continue
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
			return numberOfCategories;
		}
	}

	/**
	 * Returns the index of the class that has a durationdistribution attached.
	 * The method is meant be called on the configuration of compound
	 * riskfactors.
	 * 
	 * @param configurationFile
	 *            The File the index of the duration class must be found in.
	 * @return The index of found duration class. Null when no duration class is
	 *         founs.
	 * @throws DynamoConfigurationException
	 *             Thrown when the filestructure was unexpected.
	 */
	public static Integer extractDurationCategoryIndex(File configurationFile)
			throws DynamoConfigurationException {
		Integer durationCategoryIndex = null;
		try {
			String rootElementName = extractRootElementNameIncludingSchemaCheck(configurationFile);
			if (rootElementName != null) {
				if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
						.equals(rootElementName)) {
					XMLConfiguration configurationFromFile = new XMLConfiguration(
							configurationFile);

					// Validate the xml by xsd schema
					// WORKAROUND: clear() is put after the constructor (also
					// calls load()).
					// The config cannot be loaded twice,
					// because the contents will be doubled.
					configurationFromFile.clear();

					// Validate the xml by xsd schema
					configurationFromFile.setValidating(true);
					configurationFromFile.load();

					ConfigurationNode rootNode = configurationFromFile
							.getRootNode();
					List<?> rootChildren = rootNode
							.getChildren(XMLTagEntityEnum.DURATIONCLASS
									.getElementName());
					if (rootChildren.size() == 1) {
						ConfigurationNode durationCategoryNode = (ConfigurationNode) rootChildren
								.get(0);
						durationCategoryIndex = Integer
								.parseInt((String) durationCategoryNode
										.getValue());
					}
				}
			}
			return durationCategoryIndex;
		} catch (ConfigurationException e) {
			// Exception is not thrown again
			// because the application has to continue
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
			return durationCategoryIndex;
		}
	}
}
