package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationConfigurationDropdownsMapFactory {

	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.SimulationConfigurationDropdownsMapFactory");

	/** Context definition. */
	static String referenceDataNodeName = StandardTreeNodeLabelsEnum.REFERENCEDATA
			.getNodeLabel();
	/**
	 * ParentNodeNames for the entities dropdowns have to be created for.
	 */
	static final String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(),
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };

	static final String[] requiredPopulationFileNames = {
			StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONOVERALLDALYWEIGHTSFILE
					.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONOVERALLMORTALITYFILE
					.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONSIZEFILE.getNodeLabel() };

	public SimulationConfigurationDropdownsMapFactory() {

	}

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
	static public HashMap<String, HashMap<?, ?>> make(BaseNode selectedNode)
			throws ConfigurationException {
		HashMap theMap = new HashMap<String, HashMap<?, ?>>();
		ParentNode referenceDataNode = findReferenceDataNode(selectedNode);
		Object[] children = referenceDataNode.getChildren();
		for (Object childNode : children) {
			String validParentNodeName = returnParentNodeNameWhenValid((BaseNode) childNode);
			if (validParentNodeName != null) {
				if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel().equals(
						validParentNodeName)) {
					SimulationConfigurationDiseasesSetFactory
							.makeDiseaseNamesSet((ParentNode) childNode);
				}
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
						validParentNodeName)) {
					SimulationConfigurationRiskFactorsSetFactory
							.makeRiskFactorNamesSet((ParentNode) childNode);
				}
				if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel().equals(
						validParentNodeName)) {
					SimulationConfigurationPopulationsSetFactory
							.makePopulationsMap((ParentNode) childNode);
				}
			}
		}
		return theMap;
	}

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

	/**
	 * If the validNodeName == null, the name is not in the
	 * possibleParentNodeNames list.
	 * 
	 * @param childNode
	 * @return validParentNodeName, null when invalid.
	 * @throws ConfigurationException
	 */
	static String returnParentNodeNameWhenValid(BaseNode childNode)
			throws ConfigurationException {
		String validNodeName = null;
		for (int count = 0; count < possibleParentNodeNames.length; count++) {
			if (possibleParentNodeNames[count].equals(childNode
					.deriveNodeLabel())) {
				validNodeName = possibleParentNodeNames[count];
				break;
			}
		}
		return validNodeName;
	}

	/**
	 * Method that generates a RiskSourcePropertiesMap of sibling nodes
	 * ("RiskSource" nodes, either diseases or riskfactors) in the
	 * directorytree. The siblings are the children of a parentnode that is
	 * found by name.
	 * 
	 * @param populationsNode
	 * @param parentNodeName
	 * @return
	 * @throws ConfigurationException
	 */
	static public LinkedHashSet<String> makePopulationsMap(
			ParentNode populationsNode) throws ConfigurationException {
		LinkedHashSet<String> theSet = new LinkedHashSet<String>();
		Object[] children = populationsNode.getChildren();
		for (Object populationDirectoryNode : children) {
			boolean valid = validatePopulationDirectoryNode(populationDirectoryNode);
			if (valid) {
				theSet.add(((BaseNode) populationDirectoryNode)
						.deriveNodeLabel());
			}
		}
		return theSet;
	}

	private static boolean validatePopulationDirectoryNode(
			Object populationDirectoryNode) {
		boolean valid = false;
		if (populationDirectoryNode instanceof DirectoryNode) {
			valid = populationDirectoryIsComplete(populationDirectoryNode,
					valid);
		} else {
			log.info("Expected DirectoryNode, got: "
					+ populationDirectoryNode.getClass().getName());
		}
		return valid;
	}

	private static boolean populationDirectoryIsComplete(
			Object populationDirectoryNode, boolean valid) {
		Object[] children = ((ParentNode) populationDirectoryNode)
				.getChildren();
		boolean[] checkList = new boolean[requiredPopulationFileNames.length];
		for (Object child : children) {
			int index = findPopulationFileName((BaseNode) child);
			if (index != -1) {
				checkList[index] = true;
			}
		}
		int count = 0;
		for (; count < checkList.length; count++) {
			if (!checkList[count]) {
				break;
			}
		}
		if (count == checkList.length) {
			valid = true;
		}
		return valid;
	}

	private static int findPopulationFileName(BaseNode childNode) {
		int index = -1;
		if (childNode instanceof FileNode) {
			for (int count = 0; count < requiredPopulationFileNames.length; count++) {
				if (requiredPopulationFileNames[count].equals(childNode
						.deriveNodeLabel())) {
					index = count;
					break;
				}
			}
		} else {
			log.info("Expected FileNode, got: "
					+ childNode.getClass().getName());
		}
		return index;
	}
}