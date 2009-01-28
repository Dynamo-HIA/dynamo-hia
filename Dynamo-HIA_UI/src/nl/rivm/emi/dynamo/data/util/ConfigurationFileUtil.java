package nl.rivm.emi.dynamo.data.util;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class ConfigurationFileUtil {

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
			e.printStackTrace();
		} finally {
			return numberOfCategories;
		}
	}
}
