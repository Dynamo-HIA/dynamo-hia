package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

public class StorageTree {
	private RootNode rootNode = null;

	public StorageTree(String baseDirectoryPath) throws StorageTreeException {
		if (baseDirectoryPath == null) {
			throw new StorageTreeException("BaseDirectoryPath may not be null.");
		}
		File physicalStorage = new File(baseDirectoryPath);
		if (!physicalStorage.exists()) {
			throw new StorageTreeException("BaseDirectoryPath does not exist.");
		}
		if (physicalStorage.isDirectory()) {
			rootNode = new RootNode();
			DirectoryNode baseDirNode = new DirectoryNode(rootNode,
					physicalStorage);
			rootNode.addChild(baseDirNode);
		} else {
			throw new StorageTreeException("BaseDirectoryPath is not a directory.");
		}
	}

	public void report() {
		if (rootNode != null) {
			// rootNode.fillSet(allNodes);
			rootNode.report();
		}
	}

	public RootNode getRootNode() {
		return rootNode;
	}

}
