package nl.rivm.emi.dynamo.ui.startup;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ApplicationWrapper {
	public static final String APPBASENAME = "DYNAMO-HIA";
	private static final String RELEASE_TAG = "release 1.09 20090619";

	Log log = LogFactory.getLog(this.getClass().getName());
	Display display;
	Shell shell;

	public void startApplication() {
		BaseStorageTreeScreen application = null;
		try {
			display = new Display();
			do{
			BaseDirectoryHandler baseDirectoryHandler = new BaseDirectoryHandler(
					display);
			String baseDirectoryPath = baseDirectoryHandler.provideBaseDirectory();
			application = new BaseStorageTreeScreen(
					baseDirectoryPath);
			shell = application.open(display);
			log.debug("test completed normally.");
			if (shell != null) {
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
			} while(BaseStorageTreeScreen.RESTART.equals(application.getRestartMessage()));
			display.dispose();
		} catch (Exception e) {
			log.fatal("Exception caught: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			log.fatal("Throwable caught: " + t.getClass().getName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
		}
	}

}
