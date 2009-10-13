package czm_main;

import nl.rivm.emi.dynamo.ui.startup.ApplicationWrapper;
import nl.rivm.emi.dynamo.ui.startup.BaseDirectoryHandler;
import nl.rivm.emi.dynamo.ui.startup.BaseStorageTreeScreen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

//	private static final String APPBASENAME = "DYNAMO-HIA";
//	private static final String RELEASE_TAG = "release 1.09 20090701";
	// Logger of this class
	Log log = LogFactory.getLog(this.getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
// 20090701 Delegated functionality to get rid of code-duplication. 
//		BaseStorageTreeScreen application = null;
//		Display display = new Display();
//		Shell shell = null;
//		do{
//			BaseDirectoryHandler baseDirectoryHandler = new BaseDirectoryHandler(
//					display);
//			String baseDirectoryPath = baseDirectoryHandler.provideBaseDirectory();
//			application = new BaseStorageTreeScreen(
//					baseDirectoryPath);
//			shell = application.open(display);
//			log.debug("test completed normally.");
//			if (shell != null) {
//				shell.setText(ApplicationStatics.APPBASENAME + " " + ApplicationStatics.RELEASE_TAG);
//				shell.open();
//				while (!shell.isDisposed()) {
//					if (!display.readAndDispatch())
//						display.sleep();
//				}
//			}
//			} while(BaseStorageTreeScreen.RESTART.equals(application.getRestartMessage()));
//			display.dispose();
//		return IApplication.EXIT_OK;
		ApplicationWrapper theWrapper = new ApplicationWrapper();
		theWrapper.startApplication();
		return IApplication.EXIT_OK;
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
