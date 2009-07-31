package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskSourcePropertiesMapFactory {

	@SuppressWarnings("unused")
	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory");

	/* Context definition. */
	static String referenceDataNodeName = StandardTreeNodeLabelsEnum.REFERENCEDATA
			.getNodeLabel();

	static String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };

	/**
	 * Method that generates a RiskSourcePropertiesMap of sibling nodes
	 * ("RiskSource" nodes, either diseases or riskfactors) in the
	 * directorytree. The siblings are the children of a parentnode that is
	 * found by name.
	 * 
	 * @param selectedNode
	 *            Currently the Node from which a new
	 *            RelativeRisks-configuration is being constructed.
	 * @return
	 * @throws ConfigurationException
	 */
	static public RiskSourcePropertiesMap make(BaseNode selectedNode)
			throws ConfigurationException {
		RiskSourcePropertiesMap theMap = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		ParentNode parentOfRiskSourceNodes = getRiskSourceTypeNode(selectedNode);
		if (parentOfRiskSourceNodes != null) {
			if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
					.equalsIgnoreCase(
							((BaseNode) parentOfRiskSourceNodes)
									.deriveNodeLabel())) {
				theMap = DiseasePropertiesMapFactory
						.fillMap(parentOfRiskSourceNodes, selectedNode);
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