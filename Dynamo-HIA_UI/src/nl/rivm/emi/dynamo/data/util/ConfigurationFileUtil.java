package nl.rivm.emi.dynamo.data.util;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationFileUtil {

	private static Log log = LogFactory.getLog(ConfigurationFileUtil.class);
	
	static public String extractRootElementNameFromChildConfiguration(
			BaseNode selectedNode) throws TreeStructureException {
		String rootElementName = null;
		if (selectedNode instanceof ParentNode) {
			Object[] children = ((ParentNode) selectedNode).getChildren();
			for (Object childNode : children) {
				if (childNode instanceof BaseNode) {
					if (((BaseNode) childNode).deriveNodeLabel()
							.equalsIgnoreCase("configuration")) {
						if (childNode instanceof FileNode) {
							rootElementName = extractRootElementName(((BaseNode) childNode)
									.getPhysicalStorage());
						} else {
							throw new TreeStructureException(
									"extractRootElementNameFromChildConfiguration works only on FileNode children.");
						}
					}
				} else {
					throw new TreeStructureException(
							"extractRootElementNameFromChildConfiguration works only on BaseNode children.");

				}
			}
		} else {
			throw new TreeStructureException(
					"extractRootElementNameFromChildConfiguration works only on a ParentNode selection.");
		}
		return rootElementName;
	}

	static public String extractRootElementName(File configurationFile) {
		String rootElementName = null;
		try {
			if (configurationFile.exists()) {
				if (configurationFile.isFile()) {
					if (configurationFile.canRead()) {
						XMLConfiguration configurationFromFile;
						configurationFromFile = new XMLConfiguration(
								configurationFile);
						rootElementName = configurationFromFile
								.getRootElementName();
					}
				}
			}
		} catch (ConfigurationException e) {
			// Exception is not thrown again
			// because the application has to continue
			log.warn(e);
			e.printStackTrace();
		} finally {
			return rootElementName;
		}
	}

	public static Integer extractNumberOfClasses(File configurationFile) {
		Integer numberOfCategories = null;
		try {
			String rootElementName = extractRootElementName(configurationFile);
			if (rootElementName != null) {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName)||RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
						.equals(rootElementName)) {
					XMLConfiguration configurationFromFile = new XMLConfiguration(
							configurationFile);
					ConfigurationNode rootNode = configurationFromFile
							.getRootNode();
					ConfigurationNode ageNode = rootNode.getChild(0);
					numberOfCategories = ageNode.getChildrenCount();
				}
			}
		} catch (ConfigurationException e) {
			log.warn(e);
			e.printStackTrace();
		} finally {
			return numberOfCategories;
		}
	}
}
