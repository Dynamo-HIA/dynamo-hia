package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskSourcePropertiesMapFactory {

	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory");

	/* Context definition. */
	static String referenceDataNodeName = StandardTreeNodeLabelsEnum.REFERENCEDATA
			.getNodeLabel();

	static String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };

	/**
	 * This method must to be called from one level below one of the
	 * "RelativeRiskFromDisease" or "RelativeRiskFromRiskFactor" nodes.
	 * 
	 * It returns the RiskSourceProperties for the selected RiskSource.
	 * 
	 * @param selectedNode
	 *            Currently the Node from which a new
	 *            RelativeRisks-configuration is being constructed.
	 * @return
	 * @throws ConfigurationException
	 */
	static public RiskSourceProperties getProperties(FileNode selectedNode) throws ConfigurationException {
		String fileName = selectedNode.deriveNodeLabel();
		// Clip of the extension.
		int pointDex = fileName.indexOf(".");
		if ((fileName != null) && (pointDex != -1)) {
			fileName = fileName.substring(0, pointDex);
		}
		ParentNode parentNode = ((ChildNode) selectedNode).getParent();
		RiskSourcePropertiesMap map = makeMap4OneRiskSourceType((BaseNode) parentNode);
		Set<String> nameSet = map.keySet();
		boolean found = false;
		RiskSourceProperties props = null;
		for (String name : nameSet) {
			props = map.get(name);
			String riskSourceName = props.getRiskSourceName();
			int lastIndex = fileName.lastIndexOf(riskSourceName);
			if ((lastIndex != -1)
					&& (lastIndex == (fileName.length() - riskSourceName
							.length()))) {
				found = true;
				break;
			}
		}
		if (!found) {
			props = null;
		}
		return props;
	}

	/**
	 * This method is meant to be called with one of the
	 * "RelativeRiskFromDisease" or "RelativeRiskFromRiskFactor" nodes. Method
	 * that generates a RiskSourcePropertiesMap of sibling nodes ("RiskSource"
	 * nodes, either diseases or riskfactors) in the directorytree. The siblings
	 * are the children of a parentnode that is found by name.
	 * 
	 * @param selectedNode
	 *            Currently the Node from which a new
	 *            RelativeRisks-configuration is being constructed.
	 * @return
	 * @throws ConfigurationException
	 */
	static public RiskSourcePropertiesMap makeMap4OneRiskSourceType(
			BaseNode selectedNode) throws ConfigurationException {
		RiskSourcePropertiesMap theMap = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		if (!(StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
				.getNodeLabel().equalsIgnoreCase(selectedNodeLabel) || StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
				.getNodeLabel().equalsIgnoreCase(selectedNodeLabel))) {
			ConfigurationException exception = new ConfigurationException(
					"Method entered with wrong selectedNode: " + selectedNodeLabel);
			StackTraceElement[] elements = exception.getStackTrace();
			StringBuffer logMessage = new StringBuffer(exception.getMessage() + "\n");
			logMessage.append(exception.getClass().getSimpleName() + "\n");
			for (int count = 0; count < Math.min(elements.length, 6); count++) {
				logMessage.append(elements[count] + "\n");
			}
			log.error(logMessage.toString());
			exception = new ConfigurationException(logMessage.toString());
			throw exception;
		}
		ParentNode parentOfRiskSourceNodes = getRiskSourceTypeNode(selectedNode);
		if (parentOfRiskSourceNodes != null) {
			if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
					.equalsIgnoreCase(
							((BaseNode) parentOfRiskSourceNodes)
									.deriveNodeLabel())) {
				theMap = DiseasePropertiesMapFactory.fillMap(
						parentOfRiskSourceNodes, selectedNode);
			} else {
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
						.equalsIgnoreCase(
								((BaseNode) parentOfRiskSourceNodes)
										.deriveNodeLabel())) {
					theMap = RiskFactorPropertiesMapFactory
							.fillMap(parentOfRiskSourceNodes);
				} else {
					throw new ConfigurationException(
							"RiskSourcePropertiesMapFactory: The parentNode: "
									+ ((BaseNode) parentOfRiskSourceNodes)
											.deriveNodeLabel()
									+ " is not expected.");
				}
			}
		} else {
			throw new ConfigurationException(
					"RiskSourcePropertiesMapFactory: The parent of selected node: "
							+ selectedNodeLabel + " cannot be handled.");
		}
		return theMap;
	}

	/**
	 * Method that returns either the "diseases"node or the "risk_factors" node.
	 * Throws the ConfigurationException when no Node can be found.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static ParentNode getRiskSourceTypeNode(BaseNode selectedNode)
			throws ConfigurationException {
		BaseNode riskSourceTypeNode = null;
		String riskSourceTypeNodeName = null;
		if (selectedNode instanceof DirectoryNode) {
			riskSourceTypeNodeName = deriveRiskSourceTypeNodeName(selectedNode);
		} else {
			if (selectedNode instanceof ChildNode) {
				ParentNode parentOfSelectedNode = ((ChildNode) selectedNode)
						.getParent();
				riskSourceTypeNodeName = deriveRiskSourceTypeNodeName((BaseNode) parentOfSelectedNode);
			}
		}
		riskSourceTypeNode = findRiskSourceTypeNode(selectedNode,
				riskSourceTypeNodeName);
		if (riskSourceTypeNode == null) {
			throw new ConfigurationException(
					"RiskSourcePropertiesMapFactory: RiskSourceType node not found for node: "
							+ selectedNode.deriveNodeLabel());
		}
		return (ParentNode) riskSourceTypeNode;
	}

	private static String deriveRiskSourceTypeNodeName(BaseNode selectedNode)
			throws ConfigurationException {
		String riskSourceContainerNodeName = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
				.getNodeLabel().equalsIgnoreCase(selectedNodeLabel)) {
			riskSourceContainerNodeName = StandardTreeNodeLabelsEnum.RISKFACTORS
					.getNodeLabel();
		} else {
			if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
					.getNodeLabel().equalsIgnoreCase(selectedNodeLabel)) {
				riskSourceContainerNodeName = StandardTreeNodeLabelsEnum.DISEASES
						.getNodeLabel();
			} else {
				throw new ConfigurationException(
						"RiskSourcePropertiesMapFactory: Selected node: "
								+ selectedNodeLabel + " cannot be handled.");
			}
		}
		return riskSourceContainerNodeName;
	}

	/**
	 * The ParentNode that is requested is not nescessarily a parent of the
	 * selected node. It must be a child of the configured containerNode, so
	 * first go up to the containernode and find it by name.
	 * 
	 * @param selectedNode
	 *            TODO
	 * @param riskSourceTypeNodeName
	 *            TODO
	 * @param currentNode
	 * @return
	 */
	private static BaseNode findRiskSourceTypeNode(BaseNode selectedNode,
			String riskSourceTypeNodeName) {
		BaseNode currentNode = selectedNode;
		// First go up.
		do {
			currentNode = (BaseNode) ((ChildNode) currentNode).getParent();
			if (currentNode != null) {
				if (referenceDataNodeName.equalsIgnoreCase(currentNode
						.deriveNodeLabel())) {
					break;
				}
			}
		} while (currentNode != null);
		// and go down again.
		if (currentNode != null) {
			Object[] children = ((ParentNode) currentNode).getChildren();
			currentNode = null; // Reset.
			for (Object child : children) {
				if (riskSourceTypeNodeName.equals(((BaseNode) child)
						.deriveNodeLabel())) {
					currentNode = (BaseNode) child;
					break;
				}
			}
		}
		return currentNode;
	}
}