package nl.rivm.emi.dynamo.data.util;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return rootElementName;
		}
	}
}
