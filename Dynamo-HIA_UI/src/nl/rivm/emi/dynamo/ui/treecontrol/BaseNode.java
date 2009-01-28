package nl.rivm.emi.dynamo.ui.treecontrol;

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
	 * Returns the name of the directory/file represented by the Node minus any
	 * extensions.
	 * 
	 * @param selectedNode
	 * @return
	 */
	public String deriveNodeLabel() {
		String physicalStorageName = physicalStorage.getName();
		if (physicalStorageName != null) {
			int firstDotIndex = physicalStorageName.indexOf(".");
			int lastSlashIndex = physicalStorageName.lastIndexOf("\\");
			if (lastSlashIndex == -1) {
				lastSlashIndex = physicalStorageName.lastIndexOf("/");
			}
			if (!((lastSlashIndex != -1) && (firstDotIndex != -1) && (firstDotIndex < lastSlashIndex))) {
				if (lastSlashIndex != -1) {
					physicalStorageName = physicalStorageName.substring(
							lastSlashIndex, physicalStorageName.length());
				}
				if (firstDotIndex != -1) {
					physicalStorageName = physicalStorageName.substring(0,
							firstDotIndex);
				}
			}
		}
		return physicalStorageName;
	}

	/**
	 * Returns the name of the directory/file represented by the Node minus any
	 * extensions.
	 * 
	 * @param selectedNode
	 * @return
	 */
	public boolean isXMLFile() {
		boolean result = false;
		if (physicalStorage.isFile()) {
			String physicalStorageName = physicalStorage.getName();
			int lastDotIndex = -1;
			if ((physicalStorageName != null)
					&& ((lastDotIndex = physicalStorageName.lastIndexOf(".")) != -1)
					&& (lastDotIndex < physicalStorage.length())) {
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
