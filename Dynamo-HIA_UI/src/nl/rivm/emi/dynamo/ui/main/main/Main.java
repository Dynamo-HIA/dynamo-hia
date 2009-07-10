package nl.rivm.emi.dynamo.ui.main.main;

import nl.rivm.emi.dynamo.ui.startup.ApplicationWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Main class of the HIA_UI application This class will be called from the
 * launcher
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
	 * Main method of the HIA_UI application This method will start the
	 * application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ApplicationWrapper wrapper = new ApplicationWrapper();
			wrapper.startApplication();
		} catch (Exception e) {
			log.fatal("A fatal error occured during startup: " + e);
		}
	}

}
