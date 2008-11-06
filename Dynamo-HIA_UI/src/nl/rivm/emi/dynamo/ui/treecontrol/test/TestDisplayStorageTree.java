package nl.rivm.emi.dynamo.ui.treecontrol.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTree;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeContentProvider;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDisplayStorageTree {
	Display display;
	Shell shell;

	Log log = LogFactory.getLog(getClass().getName());
	File baseDirectory = null;

	@Before
	public void setup() {
		String baseDirectoryName = "d:/test";
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

	@After
	public void teardown() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	@Test
	public void storageTree() {
		try {
			StorageTree testTree = new StorageTree(baseDirectory
					.getAbsolutePath());
			StorageTreeContentProvider sTCP = new StorageTreeContentProvider(
					testTree.getRootNode());
			new TreeViewerPlusCustomMenu(shell, sTCP);
		} catch (StorageTreeException e) {
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestDisplayStorageTree.class);
	}
}
