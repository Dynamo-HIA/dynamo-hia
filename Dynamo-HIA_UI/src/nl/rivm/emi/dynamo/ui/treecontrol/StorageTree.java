package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
			DirectoryNode baseDirNode = new DirectoryNode(rootNode, physicalStorage);
			rootNode.addChild(baseDirNode);
		} else {
			throw new StorageTreeException("BaseDirectoryPath is no directory.");
		}
	}

	public void report() {
		if (rootNode != null) {
//			rootNode.fillSet(allNodes);
			rootNode.report();
		}
	}

	public RootNode getRootNode() {
		return rootNode;
	}
	
	private boolean templatePresent(String baseDirectoryName){
		= "d:/test";
	baseDirectory = new File(baseDirectoryName);
	try {
		if (!baseDirectory.exists()) {
			baseDirectory.mkdir();
			File.createTempFile("aaa", "bbb", baseDirectory);
			File.createTempFile("ccc", "ddd", baseDirectory);
			log.info(">>>BaseDirectory and content created.");
		} else {
			log.info(">>>BaseDirectory already exists.");
		}
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	private void createTemplate(String baseDirectoryName){
		= "d:/test";
	baseDirectory = new File(baseDirectoryName);
	try {
		if (!baseDirectory.exists()) {
			baseDirectory.mkdir();
			File.createTempFile("aaa", "bbb", baseDirectory);
			File.createTempFile("ccc", "ddd", baseDirectory);
			log.info(">>>BaseDirectory and content created.");
		} else {
			log.info(">>>BaseDirectory already exists.");
		}
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
