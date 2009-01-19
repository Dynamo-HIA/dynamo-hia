package nl.rivm.emi.dynamo.data.factories.dispatch;
/**
 * Map for dispachting manufacture of Objects from XML configuration files.
 * The configuration is contained in the DispatchEntry enumeration.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;

public class DispatchMap extends HashMap<String, AgnosticFactory > {
	private static DispatchMap instance = null;

	private DispatchMap() {
		for (DispatchEnum entry : DispatchEnum.values()) {
			put(entry.getRootNodeName(), entry.getTheFactory());
		}
	}

	synchronized public static DispatchMap getInstance() {
		if (instance == null) {
			instance = new DispatchMap();
		}
		return instance;
	}
}