package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.RootNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;

public class FactoryCommon {
	/** Context definition. */
	static String referenceDataNodeName = StandardTreeNodeLabelsEnum.REFERENCEDATA
			.getNodeLabel();

	static ParentNode findReferenceDataNode(BaseNode selectedNode)
			throws ConfigurationException {
		BaseNode workingNode = selectedNode;
		workingNode = findTheRootNode(workingNode);
		Object[] children = ((ParentNode) workingNode).getChildren();
		workingNode = null;
		boolean found = false;
		for (Object childNode : children) {
			Object[] grandChildren = ((ParentNode) childNode).getChildren();
			for (Object grandChildNode : grandChildren) {
				if (referenceDataNodeName.equals(((BaseNode) grandChildNode)
						.deriveNodeLabel())) {
					found = true;
					workingNode = (BaseNode) grandChildNode;
					break;
				}
			}
			break;
		}
		return (ParentNode) workingNode;
	}

	private static BaseNode findTheRootNode(BaseNode workingNode) {
		while (!(workingNode instanceof RootNode)) {
			workingNode = (BaseNode) ((ChildNode) workingNode).getParent();
		}
		return workingNode;
	}

	public static HashMap<String, LinkedHashSet<String>> createDropDowns(
			Object directoryNode, String[] nonEmptyDirs) {
		HashMap<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();
		Object[] children = ((ParentNode) directoryNode).getChildren();
		for (Object childNode : children) {
			if (childNode instanceof DirectoryNode) {
				String childNodeLabel = ((BaseNode) childNode)
						.deriveNodeLabel();
				LinkedHashSet<String> fileNameTrunks = null;
				for (int count = 0; count < nonEmptyDirs.length; count++) {
					if (nonEmptyDirs[count].equals(childNodeLabel)) {
						fileNameTrunks = createFileNameSet((DirectoryNode) childNode);
						break;
					}
				}
				if (fileNameTrunks != null) {
					map.put(childNodeLabel, fileNameTrunks);
				}
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
				String fileNameTrunk = ((BaseNode) childNodeObject)
				.deriveNodeLabel(); 
				if(!"".equals(fileNameTrunk)){
				fileNameTrunks.add(fileNameTrunk);
				}
			}
		}
		return fileNameTrunks;
	}
}
