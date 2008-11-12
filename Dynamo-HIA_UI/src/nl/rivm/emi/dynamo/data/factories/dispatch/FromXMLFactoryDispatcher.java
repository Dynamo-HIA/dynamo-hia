package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Class that dispatches the actual manufacturing process based on the name of 
 * the root tag of the configurationfile. 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;

public class FromXMLFactoryDispatcher {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.FromXMLFactoryDispatcher");

	static final DispatchMap dispatchMap = DispatchMap.getInstance();

	static public IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> getRelevantFactory(
			String configurationFilePath) throws ConfigurationException {
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = null;
		File configurationFile = new File(configurationFilePath);
		if (configurationFile.exists()) {
			if (configurationFile.isFile() && configurationFile.canRead()) {
				XMLConfiguration configurationFromFile;
				configurationFromFile = new XMLConfiguration(configurationFile
						.getAbsolutePath());
				String rootNodeName = configurationFromFile
						.getRootElementName();
				theFactory = dispatchMap.get(rootNodeName);
			} else {
				throw new ConfigurationException(configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			throw new ConfigurationException(configurationFilePath
					+ " does not exist, construct an empty Object.");
		}
		return theFactory;
	}

	static public IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> getRelevantFactoryByRootNodeName(
			String rootNodeName) throws ConfigurationException {
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = null;
		theFactory = dispatchMap.get(rootNodeName);
		return theFactory;
	}

	static public Object makeObservableDataObject(String configurationFilePath)
			throws ConfigurationException {
		Object theDataObject = null;
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = getRelevantFactory(configurationFilePath);
		if (theFactory != null) {
			File configurationFile = new File(configurationFilePath);
			theDataObject = theFactory.manufactureObservable(configurationFile);
			if (theDataObject == null) {
				throw new ConfigurationException(
						"DataModel could not be constructed.");
			}
		} else {
			throw new ConfigurationException(
					"No relevant factory could be found for "
							+ configurationFilePath);
		}
		return theDataObject;
	}

	static public Object makeEmptyObservableDataObject(String rootNodeName)
			throws ConfigurationException {
		Object theDataObject = null;
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = getRelevantFactoryByRootNodeName(rootNodeName);
		if (theFactory != null) {
			theDataObject = theFactory.constructObservableAllZeroesModel();
			if (theDataObject == null) {
				throw new ConfigurationException(
						"DataModel could not be constructed.");
			}
		} else {
			throw new ConfigurationException(
					"No relevant factory could be found for rootnodename "
							+ rootNodeName);
		}
		return theDataObject;
	}

	static public Object makeEmptyDataObject(String rootNodeName)
			throws ConfigurationException {
		Object theDataObject = null;
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = getRelevantFactoryByRootNodeName(rootNodeName);
		if (theFactory != null) {
			theDataObject = theFactory.constructAllZeroesModel();
			if (theDataObject == null) {
				throw new ConfigurationException(
						"DataModel could not be constructed.");
			}
		} else {
			throw new ConfigurationException(
					"No relevant factory could be found for rootnodename "
							+ rootNodeName);
		}
		return theDataObject;
	}

	static public Object makeDataObject(String configurationFilePath)
			throws ConfigurationException {
		Object theDataObject = null;
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = getRelevantFactory(configurationFilePath);
		if (theFactory != null) {
			File configurationFile = new File(configurationFilePath);
			theDataObject = theFactory.manufacture(configurationFile);
			if (theDataObject == null) {
				throw new ConfigurationException(
						"DataModel could not be constructed.");
			}
		} else {
			throw new ConfigurationException(
					"No relevant factory could be found for "
							+ configurationFilePath);
		}
		return theDataObject;
	}

	public static Object make(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting dispatch.");
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile
					.getAbsolutePath());
			String rootNodeName = configurationFromFile.getRootElementName();
			IObjectFromXMLFactory relevantFactory = dispatchMap
					.get(rootNodeName);
			Object object = relevantFactory.manufacture(configurationFile);
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

	public static Object makeObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting dispatch.");
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile
					.getAbsolutePath());
			String rootNodeName = configurationFromFile.getRootElementName();
			IObjectFromXMLFactory relevantFactory = dispatchMap
					.get(rootNodeName);
			Object object = relevantFactory
					.manufactureObservable(configurationFile);
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
