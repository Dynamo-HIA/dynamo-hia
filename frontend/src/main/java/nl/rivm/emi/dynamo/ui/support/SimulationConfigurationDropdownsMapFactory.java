package nl.rivm.emi.dynamo.ui.support;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationConfigurationDropdownsMapFactory {

	@SuppressWarnings("unused")
	private static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.util.SimulationConfigurationDropdownsMapFactory");

	/**
	 * ParentNodeNames for the entities dropdowns have to be created for.
	 */
	static final String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(),
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };

	public SimulationConfigurationDropdownsMapFactory() {

	}

	/**
	 * Method that generates a HashMap containing the current content of the tree for
	 * Populations, Diseases and RiskFactors.
	 * 
	 * @param selectedNode
	 * @param parentNodeName
	 * @return
	 * @throws ConfigurationException
	 */
	static public HashMap<String, Object> make(BaseNode selectedNode)
			throws ConfigurationException {
		HashMap<String, Object> theMap = new HashMap<String, Object>();
		ParentNode referenceDataNode = FactoryCommon.findReferenceDataNode(selectedNode);
		Object[] children = referenceDataNode.getChildren();
		for (Object childNode : children) {
			String validParentNodeName = returnParentNodeNameWhenValid((BaseNode) childNode);
			if (validParentNodeName != null) {
				if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel().equals(
						validParentNodeName)) {
					HashMap<String, HashMap<String, LinkedHashSet<String>>> diseaseMap =SimulationConfigurationDiseasesSetFactory
							.makeDiseaseNamesSet((ParentNode) childNode);
					theMap.put(StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(), diseaseMap);
				}
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
						validParentNodeName)) {
					HashMap<String, HashMap<String, LinkedHashSet<String>>> riskFactorsMap = SimulationConfigurationRiskFactorsSetFactory
							.makeRiskFactorNamesSet((ParentNode) childNode);
					theMap.put(StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), riskFactorsMap);
				}
				if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel().equals(
						validParentNodeName)) {
					LinkedHashSet<String> populationsMap = SimulationConfigurationPopulationsSetFactory
							.makePopulationsMap((ParentNode) childNode);
					theMap.put(StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), populationsMap);
				}
			}
		}
		return theMap;
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

}