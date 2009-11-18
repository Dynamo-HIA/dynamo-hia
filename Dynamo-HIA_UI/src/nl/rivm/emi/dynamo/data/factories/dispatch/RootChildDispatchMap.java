package nl.rivm.emi.dynamo.data.factories.dispatch;
/**
 * Map for dispachting manufacture of Object-parts from XML configuration files.
 * The configuration is contained in the DispatchEntry enumeration.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
/**
 * Map for finding the factory needed to manufacture Objectparts from parts of XML 
 * configuration files.
 * The key is the rootchild-elementname of the configurationfile or the FileControlEnum.
 *  
 * The configuration is contained in the RootChildDispatchEnum enumeration.
 */

public class RootChildDispatchMap extends HashMap<String, RootChildDispatchEnum > {
	private static RootChildDispatchMap instance = null;

	private RootChildDispatchMap() {
		for (RootChildDispatchEnum entry : RootChildDispatchEnum.values()) {
			put(entry.getRootNodeName(), entry);
		}
	}

	synchronized public static RootChildDispatchMap getInstance() {
		if (instance == null) {
			instance = new RootChildDispatchMap();
		}
		return instance;
	}
}