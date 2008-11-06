package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.ArrayList;

public class DirectoryNode extends BaseNode implements ParentNode, ChildNode {

	ParentNode parent = null;

	ArrayList<ChildNode> children = new ArrayList<ChildNode>();

	public DirectoryNode(ParentNode parentNode,
			File correspondingPhysicalStorage) throws StorageTreeException {
		log.info("Instantiating DirectoryNode, parent " + parentNode
				+ " physical storage " + correspondingPhysicalStorage);
		this.parent = parentNode;
		physicalStorage = correspondingPhysicalStorage;
		populateChildren();
	}

	public int populateChildren() throws StorageTreeException {
		int numberOfChildren = 0;
		if (physicalStorage != null) {
			if (physicalStorage.isDirectory()) {
				File[] childFiles = physicalStorage.listFiles();
				for (File childFile : childFiles) {
					if (childFile.isDirectory()) {
						children.add(new DirectoryNode(this, childFile));
					} else {
						children.add(new FileNode(this, childFile));
					}
				}
				numberOfChildren = children.size();
			}
		}
		return numberOfChildren;
	}

	public void report() {
		if (parent == null) {
			log.info("Tree : RootNode.");
		} else {
			log.info("Tree : " + physicalStorage.getAbsolutePath());
		}
		if (children != null) {
			for (ChildNode child : children) {
				child.report();
			}
		}
	}


	public int numberOfChildren() {
		return children.size();
	}

	public Object[] getChildren() {
		return children.toArray();
	}

	public void addChild(ChildNode childNode) throws StorageTreeException {
		children.add(childNode);
	}

	public ParentNode getParent() {
		return parent;
	}
}