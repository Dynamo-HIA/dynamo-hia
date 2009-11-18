package nl.rivm.emi.dynamo.data.factories.dispatch;

import nl.rivm.emi.dynamo.data.factories.RootLevelFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr
*
 * Allows retrieval of the Factory Object that is able to process/produce the file based on the name of 
 * the rootelementname provided. 
 */
public class FactoryProvider {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.FromXMLFactoryDispatcher");

	static final DispatchMap dispatchMap = DispatchMap.getInstance();

/**
 * @param rootNodeName
 * @return A factory Object that can be used to create a modelobject.
 * @throws ConfigurationException When no corresponding modelobject can be found.
 */
	static public RootLevelFactory getRelevantFactoryByRootNodeName(
			String rootNodeName) throws ConfigurationException {
		RootLevelFactory theFactory = null;
		theFactory = dispatchMap.get(rootNodeName).getTheFactory();
		log.debug("For rootNodeName: " + rootNodeName + " returning: " + theFactory.getClass().getSimpleName());
		return theFactory;
	}
}
