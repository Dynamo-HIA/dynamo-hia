package czm_main;

import java.io.File;

import nl.rivm.emi.dynamo.ui.main.BaseStorageTreeScreen;


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

	// Logger of this class
	static Log log = LogFactory.getLog(Application.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {

		
		Display display = new Display();
		Shell shell = null;
		
		String baseDirectoryPath = System.getProperty("user.dir")
		+ File.separator + "data" + File.separator + "dynamobase";
		
		
		
		/*
		Display display = PlatformUI.createDisplay();
		
		try {

			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			
			BaseStorageTreeScreen application = new BaseStorageTreeScreen(baseDirectoryPath);
			application.open(display);
			
			log.info("returnCode" + returnCode);
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		*/
		
		BaseStorageTreeScreen application = new BaseStorageTreeScreen(
				baseDirectoryPath);
				shell = application.open(display);
				
				// Run the thread
				if (shell != null) {								 
					shell.open();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
					display.dispose();
				}
		
				return IApplication.EXIT_OK;
		
		
	}
/*
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}	
	*/
	
	/* (non-Javadoc)
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
