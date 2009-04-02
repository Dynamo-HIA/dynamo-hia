package nl.rivm.emi.dynamo.ui.util;

import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

public class SimConDropdownCommon {

	public static HashMap<String, LinkedHashSet<String>> createDropDowns(
			Object directoryNode, String[] nonEmptyDirs) {
		HashMap<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();
		Object[] children = ((ParentNode) directoryNode)
				.getChildren();
		for (Object childNode : children) {
			if (childNode instanceof DirectoryNode) {
				String childNodeLabel = ((BaseNode) childNode)
						.deriveNodeLabel();
				LinkedHashSet<String> fileNameTrunks = null;
				for (int count = 0; count < nonEmptyDirs.length; count++) {
					if (nonEmptyDirs[count]
							.equals(childNodeLabel)) {
						fileNameTrunks = createFileNameSet((DirectoryNode) childNode);
						break;
					}
				}
				map.put(childNodeLabel, fileNameTrunks);
			}
		}
		return map;
	}

	private static LinkedHashSet<String> createFileNameSet(
			DirectoryNode childNode) {
		LinkedHashSet<String> fileNameTrunks = new LinkedHashSet<String>();
		Object[] children = ((ParentNode) childNode).getChildren();
		if (children != null) {
			for (Object childNodeObject : children) {
				fileNameTrunks.add(((BaseNode) childNodeObject)
						.deriveNodeLabel());
			}
		}
		return fileNameTrunks;
	}
}
