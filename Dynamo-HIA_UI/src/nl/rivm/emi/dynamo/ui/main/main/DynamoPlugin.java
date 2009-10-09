package nl.rivm.emi.dynamo.ui.main.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * The Plugin implementation main class
 * 
 * Defines methods to handle Plugin specific file action
 * 
 * @author schutb
 * 
 */
public abstract class DynamoPlugin extends AbstractUIPlugin {
	static Log log = LogFactory.getLog(DynamoPlugin.class);

	/**
	 * Unique ID name for the plugin
	 */
	// public static final String PLUGIN_ID = "CZM_Main";
	// All non-Eclipse plugins have been de-plugin-ified and pulled into the
	// product, non a monolith, hence the name Mono..
	public static final String PLUGIN_ID = "CZM_Mono";

	/**
	 * This plugin
	 */
	private static DynamoPlugin plugin;

	/**
	 * The Constructor
	 */
	public DynamoPlugin() {
		super();
		plugin = this;
	}

	/**
	 * 
	 * Returns the default plugin, this
	 * 
	 * @return DynamoPlugin
	 */
	public static DynamoPlugin getDefault() {
		return plugin;
	}

}
