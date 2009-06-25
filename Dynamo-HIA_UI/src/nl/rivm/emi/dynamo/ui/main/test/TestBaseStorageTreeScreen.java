package nl.rivm.emi.dynamo.ui.main.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import nl.rivm.emi.dynamo.ui.main.BaseStorageTreeScreen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBaseStorageTreeScreen {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;

	private static final String APPBASENAME = "DYNAMO-HIA";
	private static final String RELEASE_TAG = "release 1.09 20090619";
	private static final String TREE_BASE = "tree.base";
	private static final String defaultDataPath = System
			.getProperty("user.dir")
			+ File.separator
			+ ".."
			+ File.separator
			+ ".."
			+ File.separator
			+ APPBASENAME;
	private static final String iniFilePath = System.getProperty("user.dir")
			+ File.separator + "ini" + File.separator + "lastopened.ini";

	
	@Before
	public void setup() {
		display = new Display();
		log.debug("setup() complete.");
	}

	@After
	public void teardown() {
		try {
			log.debug("teardown() starts.");
			if (shell != null) {
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
			display.dispose();
			log.debug("teardown() complete.");
		} catch (Exception e) {
			log.fatal("Exception caught of type " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	@Test
	public void testBaseStorageTreeScreen() {
		log.debug("test starts.");

		try {
//			String baseDirectoryPath = System.getProperty("user.dir")
			/* + File.separator + "data" + File.separator + "dynamobase" */;
			String baseDirectoryPath = determineDataDirectory();
			BaseStorageTreeScreen application = new BaseStorageTreeScreen(
					baseDirectoryPath);
			shell = application.open(display);
			baseDirectoryPath = application.getBaseDirectoryPath();
			storeDataDirectory(baseDirectoryPath);
			assertNotNull(shell);
			log.debug("test completed normally.");
		} catch (Exception e) {
			log.debug("test caused an Exception.");
			log.fatal("Exception caught: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (Throwable t) {
			log.debug("test caused a Throwable.");
			log.fatal("Throwable caught: " + t.getClass().getName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
			assertNull(t); // Force error.
		}
	}
	
	private String determineDataDirectory() {
		String baseDirectoryPath = defaultDataPath;
		Properties iniProperties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			iniProperties.load(fis);
			String propertyDirectoryPath = iniProperties.getProperty(TREE_BASE);
			if (propertyDirectoryPath != null) {
				baseDirectoryPath = propertyDirectoryPath;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			log.fatal("Tree-Base-Directory: " + baseDirectoryPath);
			return baseDirectoryPath;
		}
	}

	private void storeDataDirectory(String baseDirectoryPath) {
		try {
			if (baseDirectoryPath != null) {
				Properties iniProperties = new Properties();
				iniProperties.put(TREE_BASE, baseDirectoryPath);
				FileOutputStream fos = new FileOutputStream(iniFilePath);
				iniProperties.store(fos, "No comment");
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

}