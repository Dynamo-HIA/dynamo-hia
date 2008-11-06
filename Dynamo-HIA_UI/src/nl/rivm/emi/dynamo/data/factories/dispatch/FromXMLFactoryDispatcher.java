package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Class that dispatches the actual manufacturing process based on the name of 
 * the root tag of the configurationfile. 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;

public class FromXMLFactoryDispatcher {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.FromXMLFactoryDispatcher");

	static final DispatchMap dispatchMap = DispatchMap.getInstance();

	static public Object makeDataObject(String configurationFilePath)
			throws ConfigurationException {
		File configurationFile = new File(configurationFilePath);
		Object theDataObject = null;
		if (configurationFile.exists()) {
			if (configurationFile.isFile() && configurationFile.canRead()) {
				theDataObject = FromXMLFactoryDispatcher
						.make(configurationFile);
				if (theDataObject == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				throw new ConfigurationException(configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			theDataObject = DispatchMap.getInstance().get("incidences")
					.constructAllZeroesModel();
		}
		return theDataObject;
	}

	public static Object make(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting dispatch.");
		XMLConfiguration configurationFromFile;
		try {
//			configurationFromFile = new XMLConfiguration(configurationFile);

			configurationFromFile = new XMLConfiguration(configurationFile.getAbsolutePath());
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			String aroundTheBend = ((DeferredElementImpl)rootNode.getReference()).getNodeName();
			ConfigurationNode firstChildNode = rootNode.getChild(0);
			ConfigurationNode parentNode = firstChildNode.getParentNode();
			String parentName = parentNode.getName();
			String rootNodeName = rootNode.getName();
			// Node firstChild = configurationFromFile.getDocument()
			// .getFirstChild();
			// String firstChildName = firstChild.getNodeName();
			IObjectFromXMLFactory relevantFactory = dispatchMap
					.get(rootNodeName);
			// .get(firstChildName);
			Object object = relevantFactory
					.manufactureFromFlatXML(configurationFile);
			return object;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			throw new ConfigurationException(exception.getMessage());
		}
	}
}
