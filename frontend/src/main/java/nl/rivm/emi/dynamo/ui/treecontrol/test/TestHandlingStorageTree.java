package nl.rivm.emi.dynamo.ui.treecontrol.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.global.StorageTree;
import nl.rivm.emi.dynamo.global.StorageTreeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHandlingStorageTree {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void teardown() {
		File[] presentFiles = baseDirectory.listFiles();
		 for (File toBeDeleted : presentFiles) {
		 toBeDeleted.delete();
		 }
		 baseDirectory.delete();
		log.info("<<<BaseDirectory and content deleted.");
	}

	@Test
	public void baseDirectoryPathNull() {
		try {
			String baseDirectoryName = null;
			StorageTree testTree = new StorageTree(baseDirectoryName);
			assertNotNull(baseDirectoryName); // Force error.
		} catch (StorageTreeException e) {
			log.info(e.getMessage());
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void baseDirectoryPathNonExistent() {
		try {
			String baseDirectoryName = "xyz:/";
			StorageTree testTree = new StorageTree(baseDirectoryName);
			assertNull(baseDirectoryName); // Force error.
		} catch (StorageTreeException e) {
			log.info(e.getMessage());
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void baseDirectoryPathOK() {
		try {
			StorageTree testTree = new StorageTree(baseDirectory
					.getAbsolutePath());
			testTree.report();
			assertNotNull(baseDirectory.getAbsolutePath()); // Force error.
		} catch (StorageTreeException e) {
			log.info(e.getMessage());
			assertNotNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestHandlingStorageTree.class);
	}

}