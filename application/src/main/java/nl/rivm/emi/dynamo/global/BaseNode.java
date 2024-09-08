package nl.rivm.emi.dynamo.global;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseNode {

	protected Log log = LogFactory.getLog(this.getClass());

	File physicalStorage = null;
	boolean menuCreated;

	public BaseNode(File physicalStorage) {
		super();
		this.physicalStorage = physicalStorage;
	}

	public File getPhysicalStorage() {
		return physicalStorage;
	}

	/**
	 * The visual representation uses this function to provide a label for the
	 * node.
	 */
	public String toString() {
		if (physicalStorage != null) {
			String absolutePath = physicalStorage.getAbsolutePath();
			int startOfLevelPart = absolutePath.lastIndexOf(File.separatorChar) + 1;
			int indexOfDotXML = absolutePath.indexOf(".xml", startOfLevelPart);
			String inLevelPath = absolutePath
					.substring(startOfLevelPart,
							indexOfDotXML != -1 ? indexOfDotXML : absolutePath
									.length());
			return inLevelPath;
		} else {
			return "null";
		}
	}

	/**
	 * Returns the name of the directory/file represented by the BaseNode minus
	 * any extensions.
	 * 
	 * @return The label for use in the Tree.
	 */
	public String deriveNodeLabel() {
		String nodeLabel = "null";
		if (physicalStorage != null) {
			nodeLabel = physicalStorage.getName();
			if (nodeLabel != null) {
				int firstDotIndex = nodeLabel.indexOf(".");
				int lastSlashIndex = nodeLabel.lastIndexOf("\\");
				if (lastSlashIndex == -1) {
					lastSlashIndex = nodeLabel.lastIndexOf("/");
				}
				if (!((lastSlashIndex != -1) && (firstDotIndex != -1) && (firstDotIndex < lastSlashIndex))) {
					if (lastSlashIndex != -1) {
						nodeLabel = nodeLabel.substring(
								lastSlashIndex, nodeLabel.length());
					}
					if (firstDotIndex != -1) {
						nodeLabel = nodeLabel.substring(0,
								firstDotIndex);
					}
				}
			}
		}
		return nodeLabel;
	}

	/**
	 * Indicates whether this node represents an XML file.<br/>
	 * The test is superficial and passes when the node represents a File and an
	 * "xml" extension is present. The "xml" is checked case insensitively.
	 * 
	 * @return Flag indicating whether the BaseNode represents an XML file.
	 */
	public boolean isXMLFile() {
		boolean result = false;
		if (physicalStorage.isFile()) {
			String physicalStorageName = physicalStorage.getName();
			int lastDotIndex = physicalStorageName.lastIndexOf(".");
			if ((physicalStorageName != null) && (lastDotIndex != -1)
					&& (lastDotIndex < physicalStorageName.length())) {
				String extension = physicalStorageName.substring(
						lastDotIndex + 1, physicalStorageName.length());
				if ("xml".equalsIgnoreCase(extension)) {
					result = true;
					
				}
			}
		}
		return result;
	}
}
