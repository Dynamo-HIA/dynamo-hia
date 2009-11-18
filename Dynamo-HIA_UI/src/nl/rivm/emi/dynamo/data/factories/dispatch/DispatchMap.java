package nl.rivm.emi.dynamo.data.factories.dispatch;
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
/**
 * Map for finding the factory needed to manufacture Objects from XML configuration files.
 * The key is the rootelementname of the configurationfile or the FileControlEnum.
 *  
 * The configuration is contained in the DispatchEnum enumeration.
 */
public class DispatchMap extends HashMap<String, DispatchEnum > {
	private static DispatchMap instance = null;

	private DispatchMap() {
		for (DispatchEnum entry : DispatchEnum.values()) {
			put(entry.getRootNodeName(), entry);
		}
	}

	synchronized public static DispatchMap getInstance() {
		if (instance == null) {
			instance = new DispatchMap();
		}
		return instance;
	}
}