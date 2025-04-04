package nl.rivm.emi.dynamo.global;

/**
 * Very small Object wrapping the RootNode, that does a lot more work...
 * 
 * test.gif
 * 
 * 20081202 RLM Checks have been removed, because the base directory is 
 * provided through a directory dialog that provides a "guaranteed" directory. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.global.StorageTreeException;

public class StorageTree {
	private RootNode rootNode = null;

	public StorageTree(String baseDirectoryPath) throws StorageTreeException {
		File physicalStorage = new File(baseDirectoryPath);
		rootNode = new RootNode();
		DirectoryNode baseDirNode = new DirectoryNode(rootNode, physicalStorage);
		rootNode.addChild(baseDirNode);
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
