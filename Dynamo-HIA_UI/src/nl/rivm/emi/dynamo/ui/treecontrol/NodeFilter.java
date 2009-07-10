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
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.treecontrol.NodeFilter");

	static public boolean putInTreeButSuppressLargeFiles(Object element) {
		boolean putInTree = false;
		try {
			File fysicalStorage = ((BaseNode) element).getPhysicalStorage();
			String name = fysicalStorage.getName();
			log.debug("Called for " + element.getClass().getSimpleName() + "  and name " + name);
			if (element instanceof DirectoryNode) {
				log.debug("DIR::: Called for DirectoryNode: " + name);
				if (!".".equals(name.substring(0, 1))) {
					putInTree = true;
				} else {
					log.debug(">>>>> " + name + " suppressed.");
				}
			} else {
				if (element instanceof FileNode) {
					// log.debug("Called for FileNode: "
					// + ((BaseNode) element).deriveNodeLabel());
					FileNode theNode = (FileNode) element;
					File physicalStorage = theNode.physicalStorage;
					// log.debug("File size: " + physicalStorage.length());
					String fileName = physicalStorage.getName();
					if (hasXMLExtension(fileName)) {
						if (physicalStorage.length() < 1000000L) {
							String rootElementName = ConfigurationFileUtil
									.justExtractRootElementName(physicalStorage);
							// 20090629 Made case-insensitive to match
							// XML-schema testing.
							RootElementNamesEnum renEnum = singleton
									.get(rootElementName.toLowerCase());
							if (renEnum != null) {
								boolean locationOK = renEnum
										.isLocationOK(theNode);
								if (locationOK) {
									putInTree = true;
								} else {
									log
											.info("File \""
													+ ((FileNode) element).physicalStorage
													+ "\" suppressed: Location in tree found wrong.");
								}
							} else {
								log.info("File \""
										+ ((FileNode) element).physicalStorage
										+ "\" suppressed: RootElementName: \""
										+ rootElementName + "\" found wrong.");
							}
						} else {
							log
									.info("File \""
											+ ((FileNode) element).physicalStorage
											+ "\" suppressed: Size: \""
											+ physicalStorage.length()
											+ "\" larger than current test can endure.");
						}
					} else {
						log.info("File \""
								+ ((FileNode) element).physicalStorage
								+ "\"suppressed: Has no XML extension.");
					}
				} else {
					log.error("Called for something else: " + element);
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
