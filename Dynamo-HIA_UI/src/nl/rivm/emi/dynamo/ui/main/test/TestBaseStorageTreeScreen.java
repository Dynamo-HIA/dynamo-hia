package nl.rivm.emi.dynamo.ui.main.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

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

	@Before
	public void setup() {
		display = new Display();
log.debug("setup() complete.");
	}

	@After
	public void teardown() {
		log.debug("teardown() starts.");
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		log.debug("teardown() complete.");
	}

	@Test
	public void testBaseStorageTreeScreen() {
		log.debug("test starts.");

		try {
			String baseDirectoryPath = System.getProperty("user.dir")
					+ File.separator + "data" + File.separator
					+ "dynamobase";
			BaseStorageTreeScreen application = new BaseStorageTreeScreen(
					baseDirectoryPath);
			shell = application.open(display);
			assertNotNull(shell);
			log.debug("test completed normally.");
		} catch (Exception e) {
			log.debug("test caused an Exception.");
			log.fatal("Exception caught: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			assertNull(e); // Force error.
		} catch (Throwable t) {
			log.debug("test caused a Throwable.");
			log.fatal("Throwable caught: " + t.getClass().getName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
			assertNull(t); // Force error.
		}
	}
}