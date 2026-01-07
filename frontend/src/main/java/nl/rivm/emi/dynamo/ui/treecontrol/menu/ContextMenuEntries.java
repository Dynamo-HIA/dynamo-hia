package nl.rivm.emi.dynamo.ui.treecontrol.menu;

import java.util.Collection;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

public class ContextMenuEntries extends
LinkedHashMap<String, ContextMenuEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setNoConfig() {
		Collection<ContextMenuEntry> entries = values();
		for (ContextMenuEntry currentEntry : entries) {
			if (!currentEntry
					.equals(get(StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
							.getNodeLabel()))) {
				currentEntry.setActive(false);
			}
		}
	}
}
