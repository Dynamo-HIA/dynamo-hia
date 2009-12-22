package nl.rivm.emi.dynamo.ui.panels.util;

import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.ui.panels.simulation.NestedTab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NestedTabsMap extends LinkedHashMap<String, NestedTab>{
	private static final long serialVersionUID = 2493820469196120469L;
private Log log = LogFactory.getLog(this.getClass().getName());

@Override
	public NestedTab put(String key, NestedTab value) {
		log.debug("Adding at key: " + key + " NestedTab.getName(): " + value.getName());
		return super.put(key, value);
	}
}
