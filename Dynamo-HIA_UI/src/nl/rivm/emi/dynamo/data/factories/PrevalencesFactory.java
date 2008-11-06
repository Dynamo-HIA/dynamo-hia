package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.PrevalencesMarker;
import nl.rivm.emi.dynamo.data.factories.dispatch.DispatchMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

public class PrevalencesFactory implements
		IObjectFromXMLFactory<PrevalencesMarker> {

	Log log = LogFactory.getLog(this.getClass().getName());

	public PrevalencesMarker constructAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public PrevalencesMarker manufactureFromFlatXML(File configurationFile)
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
		Integer catInteger = configurationFromFile.getInteger("cat", new Integer(-1));
		if ( catInteger != null) {
			log.debug("Categorical prevalences file found.");
		} else {
			if (configurationFromFile.getProperty("mean") != null) {
				log.debug("Continuous prevalences file found.");
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
}
