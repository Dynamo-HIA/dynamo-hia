package czm_main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
	// Logger of this class
	static Log log = LogFactory.getLog(Application.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {

		Display display = new Display();
		Shell shell = null;
		String baseDirectoryPath = determineDataDirectory();
		BaseStorageTreeScreen application = new BaseStorageTreeScreen(
				baseDirectoryPath);
		shell = application.open(display);
		baseDirectoryPath = application.getBaseDirectoryPath();
		// Run the thread
		if (shell != null) {
			shell.setText(APPBASENAME + " " + RELEASE_TAG);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		}

		return IApplication.EXIT_OK;

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
