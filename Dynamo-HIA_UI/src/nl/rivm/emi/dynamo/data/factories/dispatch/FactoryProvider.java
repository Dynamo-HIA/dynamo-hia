package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Class that retrieves the Factory Object that is able to process/produce the file based on the name of 
 * the root tag found in the configurationfile or provided. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FactoryProvider {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.FromXMLFactoryDispatcher");

	static final DispatchMap dispatchMap = DispatchMap.getInstance();

	static public AgnosticFactory getRelevantFactoryByRootNodeName(
			String rootNodeName) throws ConfigurationException {
		AgnosticFactory theFactory = null;
		theFactory = dispatchMap.get(rootNodeName).getTheFactory();
		return theFactory;
	}
}
