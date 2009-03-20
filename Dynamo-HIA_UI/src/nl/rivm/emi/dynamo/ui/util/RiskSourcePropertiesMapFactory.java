package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;

public class RiskSourcePropertiesMapFactory {
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
			theMap = fillMap(parentOfRiskSourceNodes);
			if(theMap.size() > 0){
			theMap = cleanMap(theMap, selectedNode);
			}
		} else {
			throw new ConfigurationException(
					"RiskSourcePropertiesMapFactory: The parent of selected node: "
							+ selectedNodeLabel + " cannot be handled.");
		}
		return theMap;
	}

	static ParentNode getParentNodeOfRelevantRiskSourceNodes(
			BaseNode selectedNode) throws ConfigurationException {
		BaseNode riskSourceTypeNode = null;
		String riskSourceTypeNodeName = null;
		if (selectedNode instanceof DirectoryNode) {
			riskSourceTypeNodeName = getRiskSourceTypeNodeName(selectedNode);
		} else {
			if (selectedNode instanceof ChildNode) {
				ParentNode parentOfSelectedNode = ((ChildNode) selectedNode)
						.getParent();
				riskSourceTypeNodeName = getRiskSourceTypeNodeName((BaseNode) parentOfSelectedNode);
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

	private static String getRiskSourceTypeNodeName(BaseNode selectedNode)
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
	 * first go up to the containernodeand find it by name.
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

	/**
	 * Now find the requested ParentNode among the children of the
	 * containerNode.
	 * 
	 * @param riskSourceTypeNode
	 * @param parentNodeName
	 * @return
	 */
	private static BaseNode findParentInRiskSourceTypeNode(
			BaseNode riskSourceTypeNode, String parentNodeName) {
		Object[] children = ((ParentNode) riskSourceTypeNode).getChildren();
		riskSourceTypeNode = null;
		for (Object child : children) {
			if (parentNodeName.equalsIgnoreCase(((BaseNode) child)
					.deriveNodeLabel())) {
				riskSourceTypeNode = (BaseNode) child;
				break;
			}
		}
		return riskSourceTypeNode;
	}

	/**
	 * Create and fill the RiskSourcePropertiesMap of the children of the found
	 * RiskSourceParentNode.
	 * 
	 * @param parentNode
	 * 
	 * @return
	 * @throws DynamoConfigurationException
	 */
	private static RiskSourcePropertiesMap fillMap(ParentNode riskSourceTypeNode)
			throws DynamoConfigurationException {
		RiskSourcePropertiesMap theMap = new RiskSourcePropertiesMap();
		if (riskSourceTypeNode != null) {
			Object[] riskSourceNodes = ((ParentNode) riskSourceTypeNode)
					.getChildren();
			if (riskSourceNodes.length != 0) {
				for (Object riskSourceNode : riskSourceNodes) {
					RiskSourceProperties properties = createRiskSourceProperties(riskSourceNode);
					if (properties != null) {
						theMap
								.put(properties.getFileNameMainPart(),
										properties);
					}
				}
			}
		}
		return theMap;
	}

	private static RiskSourceProperties createRiskSourceProperties(Object child)
			throws DynamoConfigurationException {
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
		if (!isRiskSourceADisease((ParentNode) parentNode)) {
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
			RiskSourceProperties properties)
			throws DynamoConfigurationException {
		Object[] grandChildNodes = ((ParentNode) child).getChildren();
		if (grandChildNodes.length == 0) {
			properties = null;
		} else {
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
								.getNodeLabel().equals(rootElementName)
								|| RootElementNamesEnum.RISKFACTOR_COMPOUND
										.getNodeLabel().equals(rootElementName)) {
							Integer numberOfCategories = ConfigurationFileUtil
									.extractNumberOfClasses(configurationFile);
							properties
									.setNumberOfCategories(numberOfCategories);
						}
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
	private static boolean isRiskSourceADisease(ParentNode riskSourceParentNode) {
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

	private static Integer findNumberOfCategories(Object[] children)
			throws DynamoConfigurationException {
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

	private static RiskSourcePropertiesMap cleanMap(
			RiskSourcePropertiesMap theMap, BaseNode selectedNode) {
		Set<String> keySet = theMap.keySet();
		Set<String> cloneSet = new HashSet<String>();
		cloneSet.addAll(keySet);
		Iterator<String> iterator = cloneSet.iterator();
		while (iterator.hasNext()) {
			String riskSourceName = iterator.next();
			RiskSourceProperties rSProperties = theMap.get(riskSourceName);
			ChildNode riskSourceNode = (ChildNode) rSProperties
					.getRiskSourceNode();
			ParentNode riskSourceTypeNode = riskSourceNode.getParent();
			boolean isADisease = isRiskSourceADisease(riskSourceTypeNode);
			if (isADisease) {
				String selectedDiseaseNodeLabel = null;
				String selectedNodeLabel = selectedNode.deriveNodeLabel();
				if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
						.getNodeLabel().equals(selectedNodeLabel)) {
					BaseNode parentNode = (BaseNode) ((ChildNode) selectedNode)
							.getParent();
					selectedDiseaseNodeLabel = parentNode.deriveNodeLabel();
				} else {
					BaseNode parentNode = (BaseNode) ((ChildNode) selectedNode)
							.getParent();
					BaseNode grandParentNode = (BaseNode) ((ChildNode) parentNode)
							.getParent();
					selectedDiseaseNodeLabel = grandParentNode
							.deriveNodeLabel();
				}
				String riskSourceNodeLabel = ((BaseNode) riskSourceNode)
						.deriveNodeLabel();
				if (selectedDiseaseNodeLabel.equals(riskSourceNodeLabel)) {
					theMap.remove(riskSourceName);
				}
			} else {
				String rootElementName = rSProperties.getRootElementName();
				if (rootElementName == null) {
					theMap.remove(riskSourceName);
				}
			}
		}
		return theMap;
	}
}