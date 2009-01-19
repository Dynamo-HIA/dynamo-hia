package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates an array of String-s filled with nodeLabels of ChildNodes of a certain ParentNode.
 * 
 */
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

public class RiskSourcePropertiesMapFactory {
	/* Context definition. */
	static String containerNodeName = StandardTreeNodeLabelsEnum.REFERENCEDATA
			.getNodeLabel();
	static String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };

/**
 * Method that generates an array of nodelabels of sibling nodes in the directorytree.
 * The siblings are the children of parentnode that is found by name.  
 * @param selectedNode
 * @param parentNodeName
 * @return
 */
	static public RiskSourcePropertiesMap make(BaseNode selectedNode) {
		RiskSourcePropertiesMap theMap = null;
		String parentNodeName = null;
		RiskSourcePropertiesMap list = null;
		if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
				.getNodeLabel().equalsIgnoreCase(selectedNode.deriveNodeLabel())) {
			parentNodeName = StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel();
		} else {
			if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
					.getNodeLabel().equalsIgnoreCase(selectedNode.deriveNodeLabel())) {
					parentNodeName =	StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel();
			} else {
			}
		}
		if (checkParentNodeName(parentNodeName)) {
			BaseNode currentNode = selectedNode;
			currentNode = findContainerNode(currentNode);
			if (currentNode != null) {
				currentNode = findParentNode(currentNode, parentNodeName);
				if (currentNode != null) {
					if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
							.equalsIgnoreCase(parentNodeName)) {
						theMap = fillMap(selectedNode, currentNode, true);
					} else {
						theMap = fillMap(selectedNode, currentNode, false);

					}
				}
			}
		}
		return theMap;
	}

	/**
	 * Is the provided parentNodeName one of the possibleParentNodeNames this
	 * Class knows how to handle?
	 * 
	 * @param parentNodeName
	 * @return
	 */
	private static boolean checkParentNodeName(String parentNodeName) {
		boolean found = false;
		for (int count = 0; count < possibleParentNodeNames.length; count++) {
			if (possibleParentNodeNames[count].equalsIgnoreCase(parentNodeName)) {
				found = true;
			}
		}
		return found;
	}

	/**
	 * The ParentNode that is requested is not nescessarily a parent of the
	 * selected node. It must be a child of the configured containerNode.
	 * 
	 * @param currentNode
	 * @return
	 */
	private static BaseNode findContainerNode(BaseNode currentNode) {
		{
			do {
				currentNode = (BaseNode) ((ChildNode) currentNode).getParent();
				if (currentNode != null) {
					if (containerNodeName.equalsIgnoreCase(currentNode
							.deriveNodeLabel())) {
						break;
					}
				}
			} while (currentNode != null);
		}
		return currentNode;
	}

	/**
	 * Now find the requested ParentNode among the children of the
	 * containerNode.
	 * 
	 * @param currentNode
	 * @param parentNodeName
	 * @return
	 */
	private static BaseNode findParentNode(BaseNode currentNode,
			String parentNodeName) {
		Object[] children = ((ParentNode) currentNode).getChildren();
		currentNode = null;
		for (Object child : children) {
			if (parentNodeName.equalsIgnoreCase(((BaseNode) child)
					.deriveNodeLabel())) {
				currentNode = (BaseNode) child;
				break;
			}
		}
		return currentNode;
	}

	/**
	 * Create and fill the array of nodelabels of the children of the requested
	 * parentNode.
	 * @param selectedNode TODO
	 * @param currentNode
	 * @param suppressMyLabel
	 *            TODO
	 * 
	 * @return
	 */
	private static RiskSourcePropertiesMap fillMap(BaseNode selectedNode,
			BaseNode currentNode, boolean suppressMyLabel) {
		RiskSourcePropertiesMap theMap = null;
		ParentNode parentOfSelectedNode =((ChildNode)selectedNode).getParent();
		if (currentNode != null) {
			ParentNode currentAsParentNode = (ParentNode) currentNode;
			Object[] childNodes = currentAsParentNode.getChildren();
				theMap = new RiskSourcePropertiesMap();
			for (Object child : childNodes) {
				if (!(suppressMyLabel && parentOfSelectedNode.equals(child))) {
					String name = ((BaseNode) child).deriveNodeLabel();
					RiskSourceProperties properties = new RiskSourceProperties();
					properties.setFileNameMainPart(name);
					theMap.put(name, properties);
				}
			}
		}
		return theMap;
	}
}
