package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseNode {

	protected Log log = LogFactory.getLog(this.getClass());

	File physicalStorage = null;

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
			int startOfLevelPart = absolutePath
			.lastIndexOf(File.separatorChar) + 1;
			int indexOfDotXML =  absolutePath.indexOf(".xml", startOfLevelPart);
			String inLevelPath = absolutePath.substring(startOfLevelPart, indexOfDotXML!=-1?indexOfDotXML:absolutePath.length());
			return inLevelPath;
		} else {
			return "nullll";
		}
	}
}
