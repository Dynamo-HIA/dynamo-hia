package nl.rivm.emi.dynamo.ui.treecontrol.menu;

import java.util.Collection;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

public class ContextMenuEntries extends
LinkedHashMap<String, ContextMenuEntry> {

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
