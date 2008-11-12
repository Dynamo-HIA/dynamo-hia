package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.factories.base.Factory;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.factories.base.IZeroesObjectFactory;
import nl.rivm.emi.dynamo.data.factories.base.IZeroesObjectProxy;
import nl.rivm.emi.dynamo.data.factories.base.Proxy;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.ObservablePrevalencesCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.PrevalencesCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.PrevalencesMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;
import nl.rivm.emi.dynamo.data.factories.dispatch.DispatchMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

public class PrevalencesProxy implements
		Proxy<StandardObjectMarker, ObservableObjectMarker>{

	Log log = LogFactory.getLog(this.getClass().getName());

	PrevalencesCategoricalFactory catFactory = new PrevalencesCategoricalFactory();
	HashMap<String, Factory<StandardObjectMarker, ObservableObjectMarker>> factoriesMap = new HashMap<String, Factory<StandardObjectMarker, ObservableObjectMarker>>();
	{
		factoriesMap.put("cat", catFactory);
	}

	public ObservableObjectMarker constructObservableAllZeroesModel(
			String testElementName) {
		ObservableObjectMarker dataObject = null;
		Factory<StandardObjectMarker, ObservableObjectMarker> theFactory = factoriesMap
				.get(testElementName);
		if (theFactory != null) {
			dataObject = theFactory.constructObservableAllZeroesModel();
			if (dataObject == null) {
				log.error(theFactory.getClass().getName()
						+ " constructed a null dataobject.");
			}
		}
		return dataObject;
	}

	public ObservableObjectMarker manufactureObservable(File configurationFile)
			throws ConfigurationException {
		XMLConfiguration configurationFromFile;
		configurationFromFile = new XMLConfiguration(configurationFile
				.getAbsolutePath());
		ConfigurationNode rootNode = configurationFromFile.getRootNode();
		ConfigurationNode firstChildNode = rootNode.getChild(0);
		Node firstChild = configurationFromFile.getDocument().getFirstChild();
		String firstChildName = firstChild.getNodeName();
		IObjectFromXMLFactory relevantFactory = DispatchMap.getInstance().get(
				firstChildName);
		// DoubleChecking
		if (!this.equals(relevantFactory)) {
			throw new ConfigurationException("Configuration file "
					+ configurationFile.getAbsolutePath()
					+ " with rootelement " + firstChildName
					+ " has been dispatched to " + this.getClass().getName());
		}
		// Find out subtype of configuration file.
		Integer catInteger = configurationFromFile.getInteger("cat",
				new Integer(-1));
		if (catInteger != null) {
			log.debug("Categorical prevalences file found.");
		} else {
			if (configurationFromFile.getProperty("mean") != null) {
				log.debug("Continuous prevalences file found.");
				catFactory.manufactureObservable(configurationFile);
			} else {
				if (configurationFromFile.getProperty("duration") != null) {
					log.debug("Duration prevalences file found.");
				} else {
					throw new ConfigurationException("Factory "
							+ this.getClass().getName()
							+ " doesn't know how to handle configuration file "
							+ configurationFile.getAbsolutePath());
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public StandardObjectMarker manufacture(File configurationFile) throws ConfigurationException {
		XMLConfiguration configurationFromFile;
		configurationFromFile = new XMLConfiguration(configurationFile);
		ConfigurationNode rootNode = configurationFromFile.getRootNode();
		ConfigurationNode firstChildNode = rootNode.getChild(0);
		Node firstChild = configurationFromFile.getDocument().getFirstChild();
		String firstChildName = firstChild.getNodeName();
		IObjectFromXMLFactory relevantFactory = DispatchMap.getInstance().get(
				firstChildName);
		// DoubleChecking
		if (!this.equals(relevantFactory)) {
			throw new ConfigurationException("Configuration file "
					+ configurationFile.getAbsolutePath()
					+ " with rootelement " + firstChildName
					+ " has been dispatched to " + this.getClass().getName());
		}
		// Find out subtype of configuration file.
		Integer catInteger = configurationFromFile.getInteger("cat",
				new Integer(-1));
		if (catInteger != null) {
			log.debug("Categorical prevalences file found.");
		} else {
			if (configurationFromFile.getProperty("mean") != null) {
				log.debug("Continuous prevalences file found.");
				catFactory.manufactureObservable(configurationFile);
			} else {
				if (configurationFromFile.getProperty("duration") != null) {
					log.debug("Duration prevalences file found.");
				} else {
					throw new ConfigurationException("Factory "
							+ this.getClass().getName()
							+ " doesn't know how to handle configuration file "
							+ configurationFile.getAbsolutePath());
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public StandardObjectMarker constructAllZeroesModel(String testElementName) {
		StandardObjectMarker dataObject = null;
		IZeroesObjectFactory<StandardObjectMarker, ObservableObjectMarker> theFactory = factoriesMap
				.get(testElementName);
		if (theFactory != null) {
			dataObject = theFactory.constructAllZeroesModel();
			if (dataObject == null) {
				log.error(theFactory.getClass().getName()
						+ " constructed a null dataobject.");
			}
		}
		return dataObject;
	}

}
