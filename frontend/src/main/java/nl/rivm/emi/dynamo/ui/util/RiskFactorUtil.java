package nl.rivm.emi.dynamo.ui.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfigurationToo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
				if (StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel()
						.equals(((BaseNode) selectedNode).deriveNodeLabel())) {
					Object[] children = ((ParentNode) selectedNode)
							.getChildren();
					numberOfCategories = findNumberOfCategoriesViaSimulationConfiguration(selectedNode);
				} else {
					throw new ConfigurationException(
							"RiskFactorUtil: getNumberOfRiskFactorClasses called from wrong place in the Tree: "
									+ selectedNode.deriveNodeLabel());
				}
			}
		}
		return numberOfCategories;
	}

	private static Integer findNumberOfCategoriesViaSimulationConfiguration(
			BaseNode selectedNode) throws DynamoConfigurationException {
		Integer numberOfCategories = null;
		String riskFactorName = null;
		ParentNode parentNode = ((ChildNode) selectedNode).getParent();
		riskFactorName = findSimulationConfigurationAndExctractRiskfactorName(parentNode);
		if (riskFactorName != null) {
			// "Simulations"-Node.
			ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
			// BaseDirectory-Node.
			ParentNode greatGrandParentNode = ((ChildNode) grandParentNode)
					.getParent();
			Object[] grandParentSiblings = greatGrandParentNode.getChildren();
			for (Object childNode : grandParentSiblings) {
				String childNodeLabel = ((BaseNode) childNode)
						.deriveNodeLabel();
				if (StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel()
						.equals(childNodeLabel)) {
					Object[] parentSiblings = ((ParentNode) childNode)
							.getChildren();
					for (Object childNode1 : parentSiblings) {
						String childNode1Label = ((BaseNode) childNode1)
								.deriveNodeLabel();
						if (StandardTreeNodeLabelsEnum.RISKFACTORS
								.getNodeLabel().equals(childNode1Label)) {
							Object[] parentNeighbourSiblings = ((ParentNode) childNode1)
									.getChildren();
							for (Object childNode2 : parentNeighbourSiblings) {
								String childNode2Label = ((BaseNode) childNode2)
										.deriveNodeLabel();
								if (riskFactorName.equals(childNode2Label)) {
									Object[] riskfactorChildren = ((ParentNode) childNode2)
											.getChildren();
									numberOfCategories = findNumberOfCategories(riskfactorChildren);
									break;
								}
							}
						}
					}
				}
			}
		}
		return numberOfCategories;
	}

	private static String findSimulationConfigurationAndExctractRiskfactorName(
			ParentNode grandParentNode) {
		String riskFactorName = null;
		Object[] parentSiblings = grandParentNode.getChildren();
		for (Object childNode : parentSiblings) {
			String childNodeLabel = ((BaseNode) childNode).deriveNodeLabel();
			if ("configuration".equals(childNodeLabel)) {
				File configurationFile = ((BaseNode) childNode)
						.getPhysicalStorage();
				riskFactorName = extractRiskfactorNameWithDOM(configurationFile);
			}
		}
		return riskFactorName;
	}

	private static String extractRiskfactorName(File configurationFile) {
		String riskfactorName = null;
		XMLConfigurationToo configurationFromFile;
		try {
			configurationFromFile = new XMLConfigurationToo(configurationFile);
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also
			// calls load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFromFile.clear();

			// Validate the xml by xsd schema
			configurationFromFile.setValidating(true);
			configurationFromFile.load();

			List<HierarchicalConfiguration> subConfigurations = configurationFromFile
					.configurationsAt(XMLTagEntityEnum.RISKFACTOR
							.getElementName());
			if (subConfigurations != null) {
				riskfactorName = subConfigurations.get(0).getString(
						XMLTagEntityEnum.UNIQUENAME.getElementName());
			}
			return riskfactorName;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String extractRiskfactorNameWithDOM(File configurationFile) {
		String riskfactorName = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(configurationFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc
					.getElementsByTagName(XMLTagEntityEnum.RISKFACTOR
							.getElementName());
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					System.out.println("List-Element : "
							+ eElement.getNodeName() + " value: "
							+ eElement.getNodeValue());
					NodeList childList = nNode.getChildNodes();
					for (int count = 0; count < childList.getLength(); count++) {
						Node childNode = childList.item(count);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							Element childElement = (Element) childNode;

							System.out.println("Child-Element : "
									+ childElement.getNodeName() + " value: "
									+ childElement.getNodeValue());
							if (XMLTagEntityEnum.UNIQUENAME.getElementName()
									.equals(childElement.getNodeName())) {
								NodeList grandChildList = childNode
										.getChildNodes();
								for (int count2 = 0; count2 < grandChildList
										.getLength(); count2++) {
									Node grandChildNode = grandChildList
											.item(count2);
									if (grandChildNode.getNodeType() == Node.TEXT_NODE) {
										Text grandChildText = (Text) grandChildNode;
										riskfactorName = grandChildText
												.getData();
										System.out
												.println("Grandchild-text aka riskfactorName : "
														+ riskfactorName);

									}
								}
							}
						}
					}
				}
			}
			return riskfactorName;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return riskfactorName;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return riskfactorName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return riskfactorName;
		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();

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
				&& ((RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName)) || (RootElementNamesEnum.RISKFACTOR_COMPOUND
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
			durationCategoryIndex = findDurationCategoryIndex(children);
		} else {
			if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
					((BaseNode) parentNode).deriveNodeLabel())) {
				Object[] children = ((ParentNode) startNode).getChildren();
				durationCategoryIndex = findDurationCategoryIndex(children);
			} else {
				throw new ConfigurationException(
						"RiskFactorUtil: getNumberOfRiskFactorClasses called from wrong place in the Tree: "
								+ selectedNode.deriveNodeLabel());
			}
		}
		return durationCategoryIndex;
	}

	private static Integer findDurationCategoryIndex(Object[] children)
			throws DynamoConfigurationException {
		Integer numberOfCategories = null;
		for (Object childNode : children) {
			String childNodeLabel = ((BaseNode) childNode).deriveNodeLabel();
			if ("configuration".equals(childNodeLabel)) {
				File configurationFile = ((BaseNode) childNode)
						.getPhysicalStorage();
				numberOfCategories = extractDurationCategoryIndex(configurationFile);
				break;
			}
		}
		return numberOfCategories;
	}

	public static Integer extractDurationCategoryIndex(File configurationFile)
			throws DynamoConfigurationException {
		Integer durationCategoryIndex;
		String rootElementName = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(configurationFile);
		if ((rootElementName != null)
				&& ((RootElementNamesEnum.RISKFACTOR_COMPOUND
						.getNodeLabel().equals(rootElementName)))) {
			durationCategoryIndex = ConfigurationFileUtil
					.extractDurationCategoryIndex(configurationFile);
		} else {
			durationCategoryIndex = new Integer(0);
		}
		return durationCategoryIndex;
	}
}
