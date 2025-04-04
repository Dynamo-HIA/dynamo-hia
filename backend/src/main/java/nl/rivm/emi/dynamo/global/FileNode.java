package nl.rivm.emi.dynamo.global;

import java.io.File;

import nl.rivm.emi.dynamo.global.StorageTreeException;

public class FileNode extends BaseNode implements ChildNode {

	ParentNode parent = null;

	public FileNode(ParentNode parent, File correspondingPhysicalStorage)
			throws StorageTreeException {
		super(correspondingPhysicalStorage);
		log.info("Instantiating FileNode, parent " + parent
				+ " physical storage " + correspondingPhysicalStorage);
		this.parent = parent;
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