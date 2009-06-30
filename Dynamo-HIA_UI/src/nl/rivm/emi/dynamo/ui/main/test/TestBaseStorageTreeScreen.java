package nl.rivm.emi.dynamo.ui.main.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import nl.rivm.emi.dynamo.ui.startup.ApplicationWrapper;
import nl.rivm.emi.dynamo.ui.startup.BaseStorageTreeScreen;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBaseStorageTreeScreen {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		try {
			log.debug("teardown() starts.");
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
		ApplicationWrapper wrapper = new ApplicationWrapper();
		wrapper.startApplication();
	}

}