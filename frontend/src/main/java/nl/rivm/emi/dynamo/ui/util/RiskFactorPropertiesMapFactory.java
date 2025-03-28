package nl.rivm.emi.dynamo.ui.util;

/**
 * Class that generates a Map filled with useful data about the present instances 
 * (ChildNodes) of a risksource type. (Either RiskFactor or Disease).
 * 
 */
import java.io.File;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfigurationToo;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskFactorPropertiesMapFactory {

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
	public static RiskSourcePropertiesMap fillMapForParentNode(ParentNode riskFactorsNode)
			throws DynamoConfigurationException {
		RiskSourcePropertiesMap theMap = new RiskSourcePropertiesMap();
		if (riskFactorsNode != null) {
			Object[] riskSourceNodes = ((ParentNode) riskFactorsNode)
					.getChildren();
			if (riskSourceNodes.length != 0) {
				for (Object riskSourceNode : riskSourceNodes) {
					handleRiskSourceNode(theMap, riskSourceNode);
				}
			}
		}
		return theMap;
	}

	/**
	 * Create and fill the RiskSourcePropertiesMap with the passed RiskSourceNode only.
	 * 
	 * Intended for use from relative risk for death or disability nodes.
	 * 
	 * @param riskSourceNode
	 * 
	 * @return
	 * @throws DynamoConfigurationException
	 */
	public static RiskSourcePropertiesMap fillMapForRiskFactorNode(DirectoryNode riskSourceNode)
			throws DynamoConfigurationException {
		RiskSourcePropertiesMap theMap = null;
		if (riskSourceNode != null) {
			ParentNode parentNode = ((ChildNode)riskSourceNode).getParent();
			String parentNodeName = ((BaseNode)parentNode).deriveNodeLabel();
			if(StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(parentNodeName)){
				theMap = new RiskSourcePropertiesMap();
				handleRiskSourceNode(theMap, riskSourceNode);
			} else {
				throw new DynamoConfigurationException("Method needs a RiskFactor node.");
			}
		} else {
			throw new DynamoConfigurationException("Null riskFactor node passed.");
		}
		return theMap;
	}

	private static void handleRiskSourceNode(RiskSourcePropertiesMap theMap,
			Object riskSourceNode) throws DynamoConfigurationException {
		RiskFactorProperties properties = createRiskFactorPropertiesObject(riskSourceNode);
		if (properties != null) {
			log.debug("Adding propertiesObject with key: "
					+ properties.getFileNameMainPart());
			theMap
					.put(properties.getFileNameMainPart(),
							properties);
		} else {
			log.debug("Null propertiesObject.");
		}
	}

	private static RiskFactorProperties createRiskFactorPropertiesObject(
			Object child) throws DynamoConfigurationException {
		log.debug("Entering create...");
		RiskFactorProperties properties = createSpecializedRiskFactorProperties(child);
		if (properties != null) {
			String name = ((BaseNode) child).deriveNodeLabel();
			properties.setFileNameMainPart(name);
			properties.setRiskSourceNode((BaseNode) child);
			properties.setRiskSourceName(name);
			BaseNode parentNode = (BaseNode) ((ChildNode) child).getParent();
			String parentFullName = parentNode.deriveNodeLabel();
			String parentTrucatedName = parentFullName.substring(0,
					parentFullName.length() - 1);
			properties.setRiskSourceLabel(parentTrucatedName);
		}
		return properties;
	}

	private static RiskFactorProperties createSpecializedRiskFactorProperties(
			Object child) throws DynamoConfigurationException {
		log.debug("Entering createSpecialized...");
		try {
			RiskFactorProperties theProperties = null;
			Object[] grandChildNodes = ((ParentNode) child).getChildren();
			if (grandChildNodes.length == 0) {
				theProperties = null; // Doesn't change anything, for clarity.
			} else {
				for (Object grandChildNode : grandChildNodes) {
					String grandChildNodeLabel = ((BaseNode) grandChildNode)
							.deriveNodeLabel();
					if ("configuration".equals(grandChildNodeLabel)) {
						theProperties = constructSpecializedRiskfactorProperties(grandChildNode);

					}
				}
			}
			return theProperties;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			throw new DynamoConfigurationException(e.getMessage());
		}
	}

	private static RiskFactorProperties constructSpecializedRiskfactorProperties(
			Object grandChildNode) throws DynamoConfigurationException,
			ConfigurationException {
		log.debug("Entering constructSpecialized...");
		
		RiskFactorProperties theProperties = null;
		File configurationFile = ((BaseNode) grandChildNode)
				.getPhysicalStorage();
		// Leave this in for the checking.
		String rootElementName = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(configurationFile);
		XMLConfigurationToo configurationFromFile = new XMLConfigurationToo(
				configurationFile);
		if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
				.equalsIgnoreCase(rootElementName)) {
			theProperties = new CategoricalRiskFactorProperties(rootElementName);
		} else {
			if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
					.equalsIgnoreCase(rootElementName)) {
				theProperties = new CompoundRiskFactorProperties(
						rootElementName);
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel()
						.equalsIgnoreCase(rootElementName)) {
					theProperties = new ContinuousRiskFactorProperties(
							rootElementName);
				} else {
					throw new DynamoConfigurationException(
							"createSpecializedRiskSourceProperties() - Unexpected RootElementName: "
									+ rootElementName);
				}
			}
		}
		// Initialization done, now fill it.
		fillSpecializedRiskFactorProperties(theProperties,
				configurationFromFile);
		return theProperties;
	}

	private static void fillSpecializedRiskFactorProperties(
			RiskFactorProperties theProperties,
			XMLConfigurationToo configurationFromFile) {
		log.debug("Entering fillSpecialized...");
		ConfigurationNode rootNode = configurationFromFile.getRootNode();
		List<?> rootChildren = rootNode.getChildren();
		for (Object rootChild : rootChildren) {
			ConfigurationNode rootChildConfigurationNode = (ConfigurationNode) rootChild;
			if (XMLTagEntityEnum.CLASSES.getElementName().equals(
					rootChildConfigurationNode.getName())) {
				int numberOfCategories = rootChildConfigurationNode
						.getChildrenCount();
				((CategoricalRiskFactorProperties) theProperties)
						.setNumberOfCategories(numberOfCategories);
			}
			if (XMLTagEntityEnum.REFERENCECLASS.getElementName().equals(
					rootChildConfigurationNode.getName())) {
				String referenceClassIndexString = (String) rootChildConfigurationNode
						.getValue();
				int referenceClassIndex = Integer
						.decode(referenceClassIndexString);
				((CategoricalRiskFactorProperties) theProperties)
						.setReferenceClassIndex(referenceClassIndex);
			}
			if (XMLTagEntityEnum.REFERENCEVALUE.getElementName().equals(
					rootChildConfigurationNode.getName())) {
				String referenceValueString = (String) rootChildConfigurationNode
						.getValue();
				float referenceValue = Float.parseFloat(referenceValueString);
				((ContinuousRiskFactorProperties) theProperties)
						.setReferenceValue(referenceValue);
			}
			if (XMLTagEntityEnum.DURATIONCLASS.getElementName().equals(
					rootChildConfigurationNode.getName())) {
				String durationClassIndexString = (String) rootChildConfigurationNode
						.getValue();
				int durationClassIndex = Integer
						.decode(durationClassIndexString);
				((CompoundRiskFactorProperties) theProperties)
						.setDurationClassIndex(durationClassIndex);
			}
		}
	}
}