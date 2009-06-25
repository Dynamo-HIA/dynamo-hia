package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesSingleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NodeFilter {

	static Pattern matchPattern = Pattern.compile("^.*\\.xml$");
	static RootElementNamesSingleton singleton = RootElementNamesSingleton
			.getInstance();
	static Log log = LogFactory.getLog("nl.rivm.emi.dynamo.ui.treecontrol.NodeFilter");

	static public boolean putInTreeButSuppressLargeFiles(Object element) {
		boolean putInTree = false;
		try {
			if (element instanceof DirectoryNode) {
				log.debug("Called for DirectoryNode: "
						+ ((BaseNode) element).deriveNodeLabel());
				putInTree = true;
			} else {
				if (element instanceof FileNode) {
					log.debug("Called for FileNode: "
							+ ((BaseNode) element).deriveNodeLabel());
					FileNode theNode = (FileNode) element;
					File physicalStorage = theNode.physicalStorage;
					log.debug("File size: " + physicalStorage.length());
					String fileName = physicalStorage.getName();
					if (hasXMLExtension(fileName)) {
						if(physicalStorage.length() < 1000000L){
						String rootElementName = ConfigurationFileUtil
								.justExtractRootElementName(physicalStorage);
						RootElementNamesEnum renEnum = singleton
								.get(rootElementName);
						if (renEnum != null) {
							boolean locationOK = renEnum.isLocationOK(theNode);
							if (locationOK) {
								putInTree = true;
							} else {
							}
						} else {
						}
						}
						} else {
					}
				} else {
					log.debug("Called for something else: " + element);
				}
			}
			return putInTree;
		} catch (DynamoConfigurationException e) {
			log.debug("Exception thrown: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			return putInTree;
		}
	}

	static public boolean hasXMLExtension(String fileName) {
		Matcher numericalMatcher = matchPattern.matcher(fileName);
		boolean match = numericalMatcher.matches();
		return match;
	}

}
