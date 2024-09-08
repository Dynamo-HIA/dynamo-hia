package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiseasePropertiesMapFactory {

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
	 * Create and fill the RiskSourcePropertiesMap with the children of the
	 * found RiskSourceParentNode.
	 * 
	 * @param parentNode
	 * 
	 * @return
	 * @throws DynamoConfigurationException
	 */
	public static RiskSourcePropertiesMap fillMap(
			ParentNode diseasesNode, BaseNode selectedNode)
			throws DynamoConfigurationException {
		RiskSourcePropertiesMap theMap = new RiskSourcePropertiesMap();
		if (diseasesNode != null) {
			Object[] riskSourceNodes = ((ParentNode) diseasesNode)
					.getChildren();
			if (riskSourceNodes.length != 0) {
				for (Object riskSourceNode : riskSourceNodes) {
					RiskSourceProperties properties = createDiseasePropertiesObject(riskSourceNode);
					if (properties != null) {
						theMap
								.put(properties.getFileNameMainPart(),
										properties);
					}
				}
			}
		}
		if (theMap.size() > 0) {
			theMap = cleanMap(theMap, selectedNode);
		}
		return theMap;
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
			removeDiseaseThatIsInfluenced(theMap, selectedNode, riskSourceName,
					riskSourceNode);
		}
		return theMap;
	}

	/**
	 * Configuring the disease we are working on a RiskSource should be
	 * impossible, so remove it from the Map.
	 * 
	 * @param theMap
	 * @param selectedNode
	 * @param riskSourceName
	 * @param riskSourceNode
	 */
	private static void removeDiseaseThatIsInfluenced(
			RiskSourcePropertiesMap theMap, BaseNode selectedNode,
			String riskSourceName, ChildNode riskSourceNode) {
		String selectedDiseaseNodeLabel = null;
		String selectedNodeLabel = selectedNode.deriveNodeLabel();
		if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES.getNodeLabel()
				.equals(selectedNodeLabel)) {
			BaseNode parentNode = (BaseNode) ((ChildNode) selectedNode)
					.getParent();
			selectedDiseaseNodeLabel = parentNode.deriveNodeLabel();
		} else {
			// We came here from the configuration file, so have to go two
			// levels up.
			BaseNode parentNode = (BaseNode) ((ChildNode) selectedNode)
					.getParent();
			BaseNode grandParentNode = (BaseNode) ((ChildNode) parentNode)
					.getParent();
			selectedDiseaseNodeLabel = grandParentNode.deriveNodeLabel();
		}
		String riskSourceNodeLabel = ((BaseNode) riskSourceNode)
				.deriveNodeLabel();
		if (selectedDiseaseNodeLabel.equals(riskSourceNodeLabel)) {
			theMap.remove(riskSourceName);
		}
	}

	private static RiskSourceProperties createDiseasePropertiesObject(
			Object child) throws DynamoConfigurationException {
		DiseaseProperties properties = new DiseaseProperties();
			String name = ((BaseNode) child).deriveNodeLabel();
			properties.setFileNameMainPart(name);
			properties.setRiskSourceNode((BaseNode) child);
			properties.setRiskSourceName(name);
			BaseNode parentNode = (BaseNode) ((ChildNode) child).getParent();
			String parentFullName = parentNode.deriveNodeLabel();
			String parentTrucatedName = parentFullName.substring(0,
					parentFullName.length() - 1);
			properties.setRiskSourceLabel(parentTrucatedName);
		return properties;
	}
}