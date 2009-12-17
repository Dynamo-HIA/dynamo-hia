package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesSingleton;
import nl.rivm.emi.dynamo.data.xml.structure.test.FileLocationTest;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr<br/>
 *         Makes sure not all files are shown in the TreeViewer. With the
 *         exception of one other file, only xml files are shown in the
 *         TreeViewer. For xml files a size limit of 2.5Million is implemented.
 *         As of 20091124 the "resultsObject.obj" below
 *         "Simulations/<SimulationName>/Results" is also shown. This to enable
 *         the reloading of Serialized resultData from the previous
 *         simulationrun.
 */
public class NodeFilter {

	static final float MAX_XML_FILE_SIZE = 3000000L;
	/**
	 * Pattern used for recognizing files with an "xml" extension.
	 */
	static Pattern xmlMatchPattern = Pattern.compile("^.*\\.xml$");
	/**
	 * Pattern used for recognizing files with an "obj" extension.
	 */
	static Pattern objMatchPattern = Pattern.compile("^resultsObject\\.obj$");
	/**
	 * Singleton containing all the valid rootelementnames used in the
	 * application.
	 */
	static RootElementNamesSingleton singleton = RootElementNamesSingleton
			.getInstance();
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.treecontrol.NodeFilter");

	/**
	 * Test the Objects passed to it. It returns a boolean that is true when the
	 * Node may be put it the Tree.<br/>
	 * All DirectoryNode-s but the ones that have a name starting with "." pass.<br/>
	 * All xml files with a "legal" rootelementname that are not larger than the
	 * sizelimit pass.<br/>
	 * As of 20091124 the "resultsObject.obj" at the right place also passes.
	 * 
	 * @param baseNode
	 *            Node to be tested.
	 * @return Flag indicating the Node may be put in the Tree.
	 */
	static public boolean testNode(Object baseNode) {
		boolean putInTree = false;
		try {
			File fysicalStorage = ((BaseNode) baseNode).getPhysicalStorage();
			String physicalStorageName = fysicalStorage.getName();
			log.debug("Called for " + baseNode.getClass().getSimpleName()
					+ "  and physicalStorageName " + physicalStorageName);
			if (baseNode instanceof DirectoryNode) {
				putInTree = passNonDotDirectories(putInTree,
						physicalStorageName);
			} else {
				if (baseNode instanceof FileNode) {
					log.debug("Processing FileNode: "
					 + ((BaseNode) baseNode).deriveNodeLabel());
					FileNode theNode = (FileNode) baseNode;
					File physicalStorage = theNode.physicalStorage;
					// log.debug("File size: " + physicalStorage.length());
					String fileName = physicalStorage.getName();
					if (hasXMLExtension(fileName)) {
						putInTree = passNotTooLargeXMLFiles(baseNode,
								putInTree, theNode, physicalStorage);
					} else {
						if (hasOBJExtension(fileName)) {
							putInTree = passResultsObjectFile(theNode);
						} else {
							log.info("File \""
									+ ((FileNode) baseNode).physicalStorage
									+ "\"suppressed: Has no XML extension.");
						}
					}
				} else {
					log.error("Called for something else: " + baseNode);
				}
			}
			return putInTree;
		} catch (DynamoConfigurationException e) {
			log.debug("Exception thrown: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			return putInTree;
		}
	}

	private static boolean passNotTooLargeXMLFiles(Object baseNode,
			boolean putInTree, FileNode theNode, File physicalStorage)
			throws DynamoConfigurationException {
		if (physicalStorage.length() < MAX_XML_FILE_SIZE) {
			String rootElementName = ConfigurationFileUtil
					.justExtractRootElementName(physicalStorage);
			// 20090629 Made case-insensitive to match
			// XML-schema testing.
			if (rootElementName != null) {
				if (rootElementName
						.endsWith(RootElementNamesEnum.RELATIVERISKS
								.getNodeLabel())) {
					log.debug("Just a line for debugging.");
				}
				RootElementNamesEnum renEnum = singleton.get(rootElementName
						.toLowerCase());
				if (renEnum != null) {
					boolean locationOK = renEnum.isLocationOK(theNode);
					if (locationOK) {
						putInTree = true;
					} else {
						log
								.info("File \""
										+ ((FileNode) baseNode).physicalStorage
										+ "\" suppressed: Location in tree found wrong.");
					}
				} else {
					log.info("File \"" + ((FileNode) baseNode).physicalStorage
							+ "\" suppressed: RootElementName: \""
							+ rootElementName + "\" found wrong.");
				}
			} else {
				log.info("File \"" + ((FileNode) baseNode).physicalStorage
						+ "\" suppressed: No valid RootElementName found.");
			}
		} else {
			log.fatal("File \"" + ((FileNode) baseNode).physicalStorage
					+ "\" suppressed: Size: \"" + physicalStorage.length()
					+ "\" larger than current test can endure.");
		}
		return putInTree;
	}

	private static boolean passResultsObjectFile(FileNode theNode)
			throws DynamoConfigurationException {
		boolean putInTree = false;
		ParentNode parentNode = ((ChildNode) theNode).getParent();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		FileLocationTest fileLocationTest = new FileLocationTest(
				StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel(),
				((BaseNode) grandParentNode).deriveNodeLabel(),
				StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel());
		boolean locationOK = fileLocationTest.test(theNode);
		if (locationOK) {
			putInTree = true;
		} else {
			log.info("File \"" + ((FileNode) theNode).physicalStorage
					+ "\" suppressed: Location in tree found wrong.");
		}
		return putInTree;
	}

	private static boolean passNonDotDirectories(boolean putInTree,
			String physicalStorageName) {
		log.debug("DIR::: Called for DirectoryNode: " + physicalStorageName);
		if (!".".equals(physicalStorageName.substring(0, 1))) {
			putInTree = true;
		} else {
			log.debug(">>>>> " + physicalStorageName + " suppressed.");
		}
		return putInTree;
	}

	static public boolean hasXMLExtension(String fileName) {
		Matcher numericalMatcher = xmlMatchPattern.matcher(fileName);
		boolean match = numericalMatcher.matches();
		return match;
	}

	static public boolean hasOBJExtension(String fileName) {
		Matcher numericalMatcher = objMatchPattern.matcher(fileName);
		boolean match = numericalMatcher.matches();
		return match;
	}
}
