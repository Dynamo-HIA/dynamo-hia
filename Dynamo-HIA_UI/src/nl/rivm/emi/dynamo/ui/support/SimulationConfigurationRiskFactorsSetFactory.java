package nl.rivm.emi.dynamo.ui.support;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationConfigurationRiskFactorsSetFactory {

	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.SimulationConfigurationRiskFactorsSetFactory");

	static final String[] namesOfRequiredFiles = { //
	StandardTreeNodeLabelsEnum.CONFIGURATIONFILE.getNodeLabel() };

	static final String[] namesOfRequiredNonEmptySubDirectories = { //
	StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel() //
	// ,StandardTreeNodeLabelsEnum.TRANSITION .getNodeLabel()
	};
	static final String[] namesOfSubDirectories4DropDownLists = {
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel() //
			, StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel() //
			, StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel()
	// ,StandardTreeNodeLabelsEnum.TRANSITION .getNodeLabel()
	};

	/**
	 * Method that generates a RiskSourcePropertiesMap of sibling nodes
	 * ("RiskSource" nodes, either diseases or riskfactors) in the
	 * directorytree. The siblings are the children of a parentnode that is
	 * found by name.
	 * 
	 * @param riskFactorsNode
	 * @param parentNodeName
	 * @return
	 * @throws ConfigurationException
	 */
	static public HashMap<String, HashMap<String, LinkedHashSet<String>>> makeRiskFactorNamesSet(
			ParentNode riskFactorsNode) throws ConfigurationException {
		HashMap<String, HashMap<String, LinkedHashSet<String>>> theMap = new HashMap<String, HashMap<String, LinkedHashSet<String>>>();
		Object[] children = riskFactorsNode.getChildren();
		for (Object riskFactorDirectoryNode : children) {
			boolean valid = validateRiskFactorDirectoryNode(riskFactorDirectoryNode);
			if (valid) {
				HashMap<String, LinkedHashSet<String>> dropDowns = FactoryCommon.createDropDowns(riskFactorDirectoryNode, namesOfRequiredNonEmptySubDirectories);
				theMap.put(((BaseNode) riskFactorDirectoryNode)
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

	private static boolean validateRiskFactorDirectoryNode(
			Object riskFactorDirectoryNode) {
		boolean valid = false;
		if (riskFactorDirectoryNode instanceof DirectoryNode) {
			valid = riskFactorSubDirectoriesAreComplete(riskFactorDirectoryNode);
			if (valid) {
				valid = riskFactorDirectoryIsComplete(riskFactorDirectoryNode,
						valid);
			}
		} else {
			// No error, RiskFactors contain a mix.
			log.debug("Expected DirectoryNode, got: "
					+ riskFactorDirectoryNode.getClass().getName());
		}
		return valid;
	}

	private static boolean riskFactorSubDirectoriesAreComplete(
			Object riskFactorDirectoryNode) {
		boolean valid = false;
		Object[] children = ((ParentNode) riskFactorDirectoryNode)
				.getChildren();
		boolean[] checkList = new boolean[namesOfRequiredNonEmptySubDirectories.length];
		for (Object child : children) {
			int index = findAndCheckRiskFactorSubDirectory((BaseNode) child);
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

	private static int findAndCheckRiskFactorSubDirectory(BaseNode childNode) {
		int index = -1;
		if (childNode instanceof DirectoryNode) {
			for (int count = 0; count < namesOfRequiredNonEmptySubDirectories.length; count++) {
				if (namesOfRequiredNonEmptySubDirectories[count]
						.equals(childNode.deriveNodeLabel())) {
					// At least one (configuration file ?) child.
					Object[] children = ((ParentNode) childNode).getChildren();
					if (children.length > 0) {
						index = count;
						break;
					} else {
						log.info("Missing configuration-file(s)in directory: "
								+ ((BaseNode) childNode).deriveNodeLabel());
					}
				}
			}
		} else {
			// No error.
			log.debug("Expected DirectoryNode, got: "
					+ childNode.getClass().getName());
		}
		return index;
	}

	private static boolean riskFactorDirectoryIsComplete(
			Object riskFactorDirectoryNode, boolean valid) {
		Object[] children = ((ParentNode) riskFactorDirectoryNode)
				.getChildren();
		boolean[] checkList = new boolean[namesOfRequiredFiles.length];
		for (Object child : children) {
			int index = findRiskFactorFileName((BaseNode) child);
			if (index != -1) {
				checkList[index] = true;
			}
		}
		int count = 0;
		for (; count < checkList.length; count++) {
			// A check failed.
			if (!checkList[count]) {
				break;
			}
		}
		if (count == checkList.length) {
			valid = true;
		} else {
			log.info("Not all required files were found in directory: "
					+ ((BaseNode) riskFactorDirectoryNode).deriveNodeLabel());
		}
		return valid;
	}

	private static int findRiskFactorFileName(BaseNode childNode) {
		int index = -1;
		if (childNode instanceof FileNode) {
			for (int count = 0; count < namesOfRequiredFiles.length; count++) {
				if (namesOfRequiredFiles[count].equals(childNode
						.deriveNodeLabel())) {
					index = count;
					break;
				} else {
					log.debug("FileName: " + childNode.deriveNodeLabel()
							+ " not required.");
				}
			}
		} else {
			log.debug("Expected FileNode, got: "
					+ childNode.getClass().getName());
		}
		return index;
	}

}