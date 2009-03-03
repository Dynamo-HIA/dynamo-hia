package nl.rivm.emi.dynamo.data.factories.dispatch;
/**
 * Map for dispachting manufacture of Object-parts from XML configuration files.
 * The configuration is contained in the DispatchEntry enumeration.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;

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