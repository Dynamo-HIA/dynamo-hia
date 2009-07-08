package nl.rivm.emi.dynamo.ui.startup;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ApplicationWrapper {
	// public static final String APPBASENAME = "DYNAMO-HIA";
	// private static final String RELEASE_TAG = "release 1.09 20090619";

	Log log = LogFactory.getLog(this.getClass().getName());
	Display display;
	Shell shell;

	public void startApplication() throws Exception {
		BaseStorageTreeScreen application = null;
		String baseDirectoryPath = null;
		try {
			display = new Display();
			do {
				BaseDirectoryHandler baseDirectoryHandler = new BaseDirectoryHandler(
						display);
				baseDirectoryPath = baseDirectoryHandler
						.provideBaseDirectory();
				if(baseDirectoryPath != null){
					BaseDirectory.getInstance(baseDirectoryPath);
				application = new BaseStorageTreeScreen(baseDirectoryPath);
				shell = application.open(display);
				log.debug("test completed normally.");
				if (shell != null) {
					shell.setText(ApplicationStatics.APPBASENAME + " "
							+ ApplicationStatics.RELEASE_TAG);
					shell.open();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
				}
				}
			} while ((application != null)&&BaseStorageTreeScreen.RESTART.equals(application
					.getRestartMessage()) && (baseDirectoryPath != null));
			display.dispose();
		} catch (Exception e) {
			log.fatal("Exception caught: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			// External behaviour adapted to CZM_MAIN Application.
			throw e;
		}
		// Adapted to CZM_MAIN Application.
		// catch (Throwable t) {
		// log.fatal("Throwable caught: " + t.getClass().getName()
		// + " with message: " + t.getMessage());
		// t.printStackTrace();
		// throw t;
		// }
	}

}
