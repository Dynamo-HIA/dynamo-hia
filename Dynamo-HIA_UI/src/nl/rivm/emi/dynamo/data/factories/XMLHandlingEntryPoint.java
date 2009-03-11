package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for hierarchic configuration files.
 * Current limitations: Simple Object (Integer, Float) at the deepest level.
 * 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
 * 20081117 Constructing of IObservables added.
 * 20081120 Made class abstract and external interface protected to force inheritance. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class XMLHandlingEntryPoint extends ConfigurationObjectBase {

	protected HashMap<String, IXMLHandlingLayer<?>> theHandlers = new LinkedHashMap<String, IXMLHandlingLayer<?>>();

	protected XMLHandlingEntryPoint(RootElementNamesEnum rootElement,
			boolean observable) {
		super(rootElement, observable);
	}

	private Log log = LogFactory.getLog(this.getClass().getName());

	abstract protected void fillHandlers(boolean observable)
			throws ConfigurationException;

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param configurationFilePath
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 * 
	 */
	public void manufacture(String configurationFilePath, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		synchronized (this) {
			log.debug("Starting manufacture.");
			File configurationFile = new File(configurationFilePath);
			if (!configurationFile.exists()) {
				handleRootChildren(null);
			} else {
				if (!configurationFile.isFile()) {
					throw new ConfigurationException("Configurationfile "
							+ configurationFile.getAbsolutePath()
							+ " is not a file.");
				} else {
					if (!configurationFile.canRead()) {
						throw new ConfigurationException("Configurationfile "
								+ configurationFile.getAbsolutePath()
								+ " cannot be read.");
					} else {
						XMLConfiguration configurationFromFile;
						try {
							configurationFromFile = new XMLConfiguration(
									configurationFile);
							
							// Validate the xml by xsd schema
							// WORKAROUND: clear() is put after the constructor (also calls load()). 
							// The config cannot be loaded twice,
							// because the contents will be doubled.
							configurationFromFile.clear();
							
							// Validate the xml by xsd schema
							configurationFromFile.setValidating(true);			
							configurationFromFile.load();		
							
							ConfigurationNode rootNode = configurationFromFile
									.getRootNode();
							
							// Check if the name of the first element of the file
							// is the same as that of the node name where the file is processes
							if (rootNode.getName() != null && rootNode.getName().equalsIgnoreCase(rootElementName)) {							
								List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
										.getChildren();
								handleRootChildren(rootChildren);
							} else {
								// The start/first element of the imported file does not match the node name
								throw new DynamoInconsistentDataException("The contents of the imported file does not match the node name"); 
							}							
						} catch (ConfigurationException e) {
							String dynamoErrorMessage = "Caught Exception of type: "
								+ e.getClass().getName()
								+ " with message: " + e.getMessage();
								e.printStackTrace();							
							ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
									e, configurationFile.getAbsolutePath());
							
						}/* catch (Exception exception) {
							log.error("Caught Exception of type: "
									+ exception.getClass().getName()
									+ " with message: "
									+ exception.getMessage());
							exception.printStackTrace();
						}*/
					}
				}
			}
		}
	}


	/**
	 * The concrete implementation is typespecific, so a callback to a lower
	 * level is used.
	 * 
	 * @param rootChildren
	 *            The direct children of the rootElement of the
	 *            configurationfile. When null is passed an Object with default
	 *            values must be constructed.
	 * @param makeObservable
	 * 
	 * @throws ConfigurationException
	 */
	abstract protected void handleRootChildren(
			List<ConfigurationNode> rootChildren) throws ConfigurationException;

	public void writeToFile(File outputFile) throws XMLStreamException,
			UnexpectedFileStructureException, IOException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		Writer fileWriter;
		fileWriter = new FileWriter(outputFile);
		XMLEventWriter writer = factory.createXMLEventWriter(fileWriter);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		streamEvents(null, writer, eventFactory);
		writer.flush();
	}

	public abstract void streamEvents(String value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException;
}
