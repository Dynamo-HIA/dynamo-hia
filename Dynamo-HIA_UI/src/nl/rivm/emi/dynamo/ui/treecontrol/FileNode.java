package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

public class FileNode extends BaseNode implements ChildNode {

	ParentNode parent = null;

	File physicalStorage = null;

	public FileNode(ParentNode parent, File correspondingPhysicalStorage)
			throws StorageTreeException {
		log.info("Instantiating FileNode, parent " + parent
				+ " physical storage " + correspondingPhysicalStorage);
		this.parent = parent;
		physicalStorage = correspondingPhysicalStorage;
	}

	public void report() {
		log.info("Tree : " + physicalStorage.getAbsolutePath());
	}

	public File getPhysicalStorage() {
		return physicalStorage;
	}

	public ParentNode getParent() {
		return parent;
	}
}