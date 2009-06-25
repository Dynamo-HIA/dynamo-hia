package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.ArrayList;

import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardDirectoryStructureHandler;

public class DirectoryNode extends BaseNode implements ParentNode, ChildNode {

	ParentNode parent = null;

	ArrayList<ChildNode> children = new ArrayList<ChildNode>();

	public DirectoryNode(ParentNode parentNode,
			File correspondingPhysicalStorage) throws StorageTreeException {
		super(correspondingPhysicalStorage);
		log.info("Instantiating DirectoryNode, parent " + parentNode
				+ " physical storage " + correspondingPhysicalStorage);
		this.parent = parentNode;
		updateStandardStructure();
		populateChildren();
	}

	public void updateStandardStructure() throws StorageTreeException {
		StandardDirectoryStructureHandler.process(this);
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
						FileNode newChildNode = new FileNode(this, childFile);
						if (NodeFilter.putInTreeButSuppressLargeFiles(newChildNode)) {
							children.add(newChildNode);
						}
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

	@Override
	public int removeChild(ChildNode storageTreeNode) {
		int numberRemoved = 0;
		for (int count = 0; count < children.size(); count++) {
			ChildNode childNode = children.get(count);
			if ((childNode != null) && (childNode.equals(storageTreeNode))) {
				children.remove(count);
				numberRemoved++;
				break;
			}
		}
		return numberRemoved;
	}

	public int numberOfChildren() {
		return children.size();
	}

	public Object[] getChildren() {
		return children.toArray();
	}

	public void addChild(ChildNode childNode) throws StorageTreeException {
		if (NodeFilter.putInTreeButSuppressLargeFiles(childNode)) {
			children.add(childNode);
		}
	}

	public ParentNode getParent() {
		return parent;
	}

}