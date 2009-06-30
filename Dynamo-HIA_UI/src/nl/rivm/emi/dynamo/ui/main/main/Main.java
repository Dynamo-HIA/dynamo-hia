package nl.rivm.emi.dynamo.ui.main.main;

import java.io.File;

import nl.rivm.emi.dynamo.ui.startup.BaseStorageTreeScreen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Main class of the HIA_UI application
 * This class will be called from the launcher
 * 
 * 
 * @date 03-02-2009
 * @author schutb
 *
 */
public class Main {
	
	// Logger of this class
	static Log log = LogFactory.getLog(Main.class);
		
	/**
	 * Main method of the HIA_UI application
	 * This method will start the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			Display display = new Display();
			Shell shell = null;
			
			String baseDirectoryPath = System.getProperty("user.dir")
			+ File.separator + "data" + File.separator + "dynamobase";
			log.info("baseDirectoryPath" + baseDirectoryPath);
			
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
		} catch (Exception e) {
			log.fatal("A fatal error occured during startup: " + e);
		}
	}
	
	
	
	
}
