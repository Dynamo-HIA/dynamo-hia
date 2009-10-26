package nl.rivm.emi.dynamo.ui.util;

import java.io.File;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;

public class RiskFactorUtil {

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
						"RiskFactorUtil: getNumberOfRiskFactorClasses called from wrong place in the Tree: "
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
				numberOfCategories = extractNumberOfCategories(configurationFile);
				break;
			}
		}
		return numberOfCategories;
	}

	public static Integer extractNumberOfCategories(File configurationFile)
			throws DynamoConfigurationException {
		Integer numberOfCategories;
		String rootElementName = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(configurationFile);
		if ((rootElementName != null)
				&& ((RootElementNamesEnum.RISKFACTOR_CATEGORICAL
						.getNodeLabel().equals(rootElementName)) || (RootElementNamesEnum.RISKFACTOR_COMPOUND
						.getNodeLabel().equals(rootElementName)))) {
			numberOfCategories = ConfigurationFileUtil
					.extractNumberOfClasses(configurationFile);
		} else {
			numberOfCategories = new Integer(0);
		}
		return numberOfCategories;
	}

	/**
	 * Method that returns the index of the durationclass (if any). throws an
	 * Exception if no durationclass is present (abuse of the method).
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static public Integer getDurationCategoryIndex(BaseNode selectedNode)
			throws ConfigurationException {
		Integer durationCategoryIndex = null;
		BaseNode startNode = null;
		if (selectedNode instanceof FileNode) {
			startNode = (BaseNode) ((ChildNode) selectedNode).getParent();
		} else {
			startNode = selectedNode;
		}
		ParentNode parentNode = ((ChildNode) startNode).getParent();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
				((BaseNode) grandParentNode).deriveNodeLabel())) {
			Object[] children = parentNode.getChildren();
			durationCategoryIndex = findNumberOfCategories(children);
		} else {
			if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
					((BaseNode) parentNode).deriveNodeLabel())) {
				Object[] children = ((ParentNode) startNode).getChildren();
				durationCategoryIndex = findNumberOfCategories(children);
			} else {
				throw new ConfigurationException(
						"RiskFactorUtil: getNumberOfRiskFactorClasses called from wrong place in the Tree: "
								+ selectedNode.deriveNodeLabel());
			}
		}
		return durationCategoryIndex;
	}
}
