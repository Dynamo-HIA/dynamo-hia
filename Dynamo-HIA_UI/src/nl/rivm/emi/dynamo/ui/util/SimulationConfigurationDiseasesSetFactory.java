package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationConfigurationDiseasesSetFactory {

	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.SimulationConfigurationDiseasesSetFactory");

	static final String[] namesOfRequiredNonEmptySubDirectories = {
			StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel(),
			StandardTreeNodeLabelsEnum.EXCESSMORTALITIES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
					.getNodeLabel() };
	
	static final String[] namesOfSubDirectories4DropDownLists = {
		StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel(),
		StandardTreeNodeLabelsEnum.EXCESSMORTALITIES.getNodeLabel(),
		StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel(),
		StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(),
		StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(),
		StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
				.getNodeLabel() };

	/**
	 * Method that generates a RiskSourcePropertiesMap of sibling nodes
	 * ("RiskSource" nodes, either diseases or riskfactors) in the
	 * directorytree. The siblings are the children of a parentnode that is
	 * found by name.
	 * 
	 * @param diseasesNode
	 * @param parentNodeName
	 * @return
	 * @throws ConfigurationException
	 */
	static public HashMap<String, HashMap<String, LinkedHashSet<String>>> makeDiseaseNamesSet(
			ParentNode diseasesNode) throws ConfigurationException {
		HashMap<String, HashMap<String, LinkedHashSet<String>>> theMap = new HashMap<String, HashMap<String, LinkedHashSet<String>>>();
		Object[] children = diseasesNode.getChildren();
		for (Object diseaseDirectoryNode : children) {
			boolean valid = validateDiseaseDirectoryNode(diseaseDirectoryNode);
			if (valid) {
				HashMap<String, LinkedHashSet<String>> dropDowns = SimConDropdownCommon.createDropDowns(diseaseDirectoryNode, namesOfRequiredNonEmptySubDirectories);
				theMap.put(((BaseNode) diseaseDirectoryNode)
						.deriveNodeLabel(), dropDowns);
			}
		}
		// Debugging code.
		StringBuffer namesConcat = new StringBuffer();
		for (String name : theMap.keySet()) {
			namesConcat.append("EntityName: " + name + "\n");
			HashMap<String, LinkedHashSet<String>> innerMap = theMap.get(name);
			for (String innerName : innerMap.keySet()) {
				namesConcat.append("\tDirectoryName: " + innerName + "\n");
				LinkedHashSet<String> innerSet = innerMap.get(innerName);
				if (innerSet != null) {
					for (String innermostName : innerSet) {
						namesConcat.append("\t\tFileName: " + innermostName
								+ "\n");
					}
				}
			}
		}
		log.debug("Made RiskFactorsSet containing:\n"
				+ namesConcat.toString());
		// Debugging code ends.
		return theMap;
	}

	private static boolean validateDiseaseDirectoryNode(
			Object diseaseDirectoryNode) {
		boolean valid = false;
		if (diseaseDirectoryNode instanceof DirectoryNode) {
			valid = diseaseSubDirectoriesAreComplete(diseaseDirectoryNode,
					valid);
		} else {
			log.info("Expected DirectoryNode, got: "
					+ diseaseDirectoryNode.getClass().getName());
		}
		return valid;
	}

	private static boolean diseaseSubDirectoriesAreComplete(
			Object populationDirectoryNode, boolean valid) {
		Object[] children = ((ParentNode) populationDirectoryNode)
				.getChildren();
		boolean[] checkList = new boolean[namesOfRequiredNonEmptySubDirectories.length];
		for (Object child : children) {
			int index = findAndCheckDiseaseSubDirectory((BaseNode) child);
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

	private static int findAndCheckDiseaseSubDirectory(BaseNode childNode) {
		int index = -1;
		if (childNode instanceof DirectoryNode) {
			for (int count = 0; count < namesOfRequiredNonEmptySubDirectories.length; count++) {
				if (namesOfRequiredNonEmptySubDirectories[count].equals(childNode
						.deriveNodeLabel())) {
					// At least one (configuration file ?) child.
					Object[] children = ((ParentNode) childNode).getChildren();
					if (children.length > 0) {
						index = count;
						break;
					}
				}
			}
		} else {
			log.info("Expected DirectoryNode, got: "
					+ childNode.getClass().getName());
		}
		return index;
	}

}