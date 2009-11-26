package nl.rivm.emi.dynamo.data.factories.dispatch;

import java.util.HashMap;

/**
 * @author mondeelr <br/>
 * 
 *         Map for finding the factory needed to manufacture Objects from XML
 *         configuration files. The key is the rootelementname of the
 *         configurationfile or the FileControlEnum.
 * 
 *         The configuration is contained in the DispatchEnum enumeration.
 */
public class DispatchMap extends HashMap<String, DispatchEnum> {
	private static final long serialVersionUID = 3787755234919713373L;
	/**
	 * The one and only instance of this Map.
	 */
	private static DispatchMap instance = null;

	/**
	 * Private constructor to prevent non-singleton use.
	 */
	private DispatchMap() {
		for (DispatchEnum entry : DispatchEnum.values()) {
			put(entry.getRootNodeName(), entry);
		}
	}

	/**
	 * Returns the one and only instance of this Map. Creates it if
	 * nescessary.
	 * 
	 * @return The (possibly new) instance to this Map.
	 */
	synchronized public static DispatchMap getInstance() {
		if (instance == null) {
			instance = new DispatchMap();
		}
		return instance;
	}
}