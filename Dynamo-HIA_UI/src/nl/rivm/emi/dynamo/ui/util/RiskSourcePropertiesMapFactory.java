package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates an array of String-s filled with nodeLabels of ChildNodes of a certain ParentNode.
 * 
 */
import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
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
	 * Method that generates a RiskSourcePropertiesMap of sibling nodes
	 * ("RiskSource" nodes, either diseases or riskfactors) in the
	 * directorytree. The siblings are the children of a parentnode that is
	 * found by name.
	 * 
	 * @param selectedNode
	 * @param parentNodeName
	 * @return
	 * @throws ConfigurationException
	 */
	static public RiskSourcePropertiesMap make(BaseNode selectedNode)
			throws ConfigurationException {
		RiskSourcePropertiesMap theMap = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		ParentNode parentOfRiskSourceNodes = getParentNodeOfRelevantRiskSourceNodes(selectedNode);
		if (parentOfRiskSourceNodes != null) {
			theMap = fillMap(selectedNode, parentOfRiskSourceNodes);
		} else {
			throw new ConfigurationException(
					"RiskSourcePropertiesMapFactory: The parent of selected node: "
							+ selectedNodeLabel + " cannot be handled.");
		}
		return theMap;
	}

	static ParentNode getParentNodeOfRelevantRiskSourceNodes(
			BaseNode selectedNode) throws ConfigurationException {
		String selectedNodeLabel = selectedNode.deriveNodeLabel();

		String parentOfSelectedRiskSourceNodeName = null;
		ParentNode parentNode = null;
		ParentNode parentOfSelectedNode = null;
		if (selectedNode instanceof ChildNode) {
			parentOfSelectedNode = ((ChildNode) selectedNode).getParent();
		}
		if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
				.getNodeLabel().equalsIgnoreCase(selectedNodeLabel)
				|| ((parentOfSelectedNode != null) && (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
						.getNodeLabel().equalsIgnoreCase(parentOfSelectedNode
						.toString())))) {
			parentOfSelectedRiskSourceNodeName = StandardTreeNodeLabelsEnum.RISKFACTORS
					.getNodeLabel();
		} else {
			if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
					.getNodeLabel().equalsIgnoreCase(selectedNodeLabel)
					|| ((parentOfSelectedNode != null) && (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
							.getNodeLabel()
							.equalsIgnoreCase(parentOfSelectedNode.toString())))) {
				parentOfSelectedRiskSourceNodeName = StandardTreeNodeLabelsEnum.DISEASES
						.getNodeLabel();
			} else {
				throw new ConfigurationException(
						"RiskSourcePropertiesMapFactory: Selected node: "
								+ selectedNodeLabel + " cannot be handled.");
			}
		}
		BaseNode containerNode = findContainerNode(selectedNode);
		if (containerNode != null) {
			parentNode = (ParentNode) findParentInContainerNode(containerNode,
					parentOfSelectedRiskSourceNodeName);
		} else {
			throw new ConfigurationException(
					"RiskSourcePropertiesMapFactory: Container node not found for node: "
							+ selectedNodeLabel);
		}

		return parentNode;
	}

	/**
	 * The ParentNode that is requested is not nescessarily a parent of the
	 * selected node. It must be a child of the configured containerNode, so
	 * first go up to the containernodeand find it by name.
	 * 
	 * @param currentNode
	 * @return
	 */
	private static BaseNode findContainerNode(BaseNode selectedNode) {
		BaseNode currentNode = selectedNode;
		do {
			currentNode = (BaseNode) ((ChildNode) currentNode).getParent();
			if (currentNode != null) {
				if (containerNodeName.equalsIgnoreCase(currentNode
						.deriveNodeLabel())) {
					break;
				}
			}
		} while (currentNode != null);
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
	private static BaseNode findParentInContainerNode(BaseNode currentNode,
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
	 * Create and fill the RiskSourcePropertiesMap of the children of the found
	 * RiskSourceParentNode.
	 * 
	 * @param selectedNode
	 * @param parentNode
	 * @return
	 * @throws DynamoConfigurationException 
	 */
	private static RiskSourcePropertiesMap fillMap(BaseNode selectedNode,
			ParentNode riskSourceParentNode) throws DynamoConfigurationException {
		// A disease cannot influence itself through a relative risk.
		boolean riskSourceIsADisease = isTheRiskSourceADisease(riskSourceParentNode);
		RiskSourcePropertiesMap theMap = null;
		ParentNode parentOfSelectedNode = ((ChildNode) selectedNode)
				.getParent(); // The "diseasename" node for now.
		if (riskSourceParentNode != null) {
			Object[] childNodes = ((ParentNode) riskSourceParentNode)
					.getChildren();
			theMap = new RiskSourcePropertiesMap();
			for (Object child : childNodes) {
				if (!(riskSourceIsADisease && parentOfSelectedNode
						.equals(child))) {
					RiskSourceProperties properties = createRiskSourceProperties(
							riskSourceIsADisease, child);
					theMap.put(properties.getFileNameMainPart(), properties);
				}
			}
		}
		return theMap;
	}

	private static RiskSourceProperties createRiskSourceProperties(
			boolean riskSourceIsADisease, Object child) throws DynamoConfigurationException {
		RiskSourceProperties properties = new RiskSourceProperties();
		String name = ((BaseNode) child).deriveNodeLabel();
		properties.setFileNameMainPart(name);
		properties.setRiskSourceNode((BaseNode) child);
		properties.setRiskSourceName(name);
		BaseNode parentNode = (BaseNode) ((ChildNode) child).getParent();
		String parentFullName = parentNode.deriveNodeLabel();
		String parentTrucatedName = parentFullName.substring(0, parentFullName
				.length() - 1);
		properties.setRiskSourceLabel(parentTrucatedName);		
		if (!riskSourceIsADisease) {
			addRiskFactorConfigurationFileInfo(child, properties);
		}
		return properties;
	}

	/**
	 * Only RiskFactors can have (three) different configuration structures.
	 * 
	 * @param child
	 * @param properties
	 * @throws DynamoConfigurationException 
	 */
	private static void addRiskFactorConfigurationFileInfo(Object child,
			RiskSourceProperties properties) throws DynamoConfigurationException {
		Object[] grandChildNodes = ((ParentNode) child).getChildren();
		for (Object grandChildNode : grandChildNodes) {
			String grandChildNodeLabel = ((BaseNode) grandChildNode)
					.deriveNodeLabel();
			if ("configuration".equals(grandChildNodeLabel)) {
				File configurationFile = ((BaseNode) grandChildNode)
						.getPhysicalStorage();
				String rootElementName = ConfigurationFileUtil
						.extractRootElementName(configurationFile);
				if (rootElementName != null) {
					properties.setRootElementName(rootElementName);
					if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL
							.getNodeLabel().equals(rootElementName)) {
						Integer numberOfCategories = ConfigurationFileUtil
								.extractNumberOfClasses(configurationFile);
						properties.setNumberOfCategories(numberOfCategories);
					}
				}
			}
		}
	}

	/**
	 * This method creates the boolean that is used to distinguish the
	 * RiskSources in Diseases and non-Diseases (in practice RiskFactors).
	 * 
	 * @param riskSourceParentNode
	 * @return
	 */
	private static boolean isTheRiskSourceADisease(
			ParentNode riskSourceParentNode) {
		boolean riskSourceIsADisease = false;
		if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
				.equalsIgnoreCase(
						((BaseNode) riskSourceParentNode).deriveNodeLabel())) {
			riskSourceIsADisease = true;
		}
		return riskSourceIsADisease;
	}

	/**
	 * Method that returns the number of classes contained in the RiskFactor
	 * configuration. It should be called with a node representing a sibling of
	 * the RiskFactor configurationfile (for now). When called for an unexpected
	 * location in the tree an Exception is throws. When called for a continuous
	 * RiskFactor the Integer contains zero.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static public Integer getNumberOfRiskFactorClasses(BaseNode selectedNode)
			throws ConfigurationException {
		Integer numberOfCategories = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		ParentNode parentNode = ((ChildNode) selectedNode).getParent();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
				((BaseNode) grandParentNode).deriveNodeLabel())) {
			Object[] children = parentNode.getChildren();
			numberOfCategories = findNumberOfCategories(children);
		} else {
			if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
					((BaseNode) parentNode).deriveNodeLabel())) {
				Object[] children = ((ParentNode) selectedNode).getChildren();
				numberOfCategories = findNumberOfCategories(children);
			} else {
				throw new ConfigurationException(
						"RiskSourcePropertiesMapFactory: getNumberOfRiskFactorClasses called from wrong place in the Tree: "
								+ selectedNode.deriveNodeLabel());
			}
		}
		return numberOfCategories;
	}

	private static Integer findNumberOfCategories(Object[] children) throws DynamoConfigurationException {
		Integer numberOfCategories = null;
		for (Object childNode : children) {
			String childNodeLabel = ((BaseNode) childNode).deriveNodeLabel();
			if ("configuration".equals(childNodeLabel)) {
				File configurationFile = ((BaseNode) childNode)
						.getPhysicalStorage();
				String rootElementName = ConfigurationFileUtil
						.extractRootElementName(configurationFile);
				if ((rootElementName != null)
						&& ((RootElementNamesEnum.RISKFACTOR_CATEGORICAL
								.getNodeLabel().equals(rootElementName)) || (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(rootElementName)))) {
					numberOfCategories = ConfigurationFileUtil
							.extractNumberOfClasses(configurationFile);
				} else {
					numberOfCategories = new Integer(0);
				}
				break;
			}
		}
		return numberOfCategories;
	}
}