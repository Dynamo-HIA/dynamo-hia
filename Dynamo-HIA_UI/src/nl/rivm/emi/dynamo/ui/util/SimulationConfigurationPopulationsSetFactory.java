package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationConfigurationPopulationsSetFactory {

	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.SimulationConfigurationPopulationsMapFactory");

	static final String[] requiredPopulationFileNames = {
			StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONOVERALLDALYWEIGHTSFILE
					.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONOVERALLMORTALITYFILE
					.getNodeLabel(),
			StandardTreeNodeLabelsEnum.POPULATIONSIZEFILE.getNodeLabel() };


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
		// Debugging code.
		StringBuffer namesConcat = new StringBuffer();
		for (String name : theSet) {
			namesConcat.append(name + ", ");
		}
		log.fatal("Made PopulationsSet containing names: "
				+ namesConcat.toString());
		// Debugging code ends.

		return theSet;
	}

	private static boolean validatePopulationDirectoryNode(
			Object populationDirectoryNode) {
		boolean valid = false;
		if (populationDirectoryNode instanceof DirectoryNode) {
			valid = populationDirectoryIsComplete(populationDirectoryNode, valid);
		} else {
			log.info("Expected DirectoryNode, got: "
					+ populationDirectoryNode.getClass().getName());
		}
		return valid;
	}

	private static boolean populationDirectoryIsComplete(Object populationDirectoryNode,
			boolean valid) {
		Object[] children = ((ParentNode) populationDirectoryNode)
				.getChildren();
		boolean[] checkList = new boolean[requiredPopulationFileNames.length];
		for(Object child:children){
			int index = findPopulationFileName((BaseNode) child);
			if(index != -1){
				checkList[index] = true;
			}
		}
		int count = 0;
		for(; count < checkList.length; count++){
			if(!checkList[count]){
				break;
			}
		}
		if(count == checkList.length){
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