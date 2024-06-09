package nl.rivm.emi.dynamo.ui.main.main;

import java.awt.Toolkit;

import nl.rivm.emi.dynamo.ui.startup.ApplicationWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * Main class of the HIA_UI application This class will be called from the
 * launcher
 * 
 * overalldalyweights
 * @date 03-02-2009
 * @author schutb
 * 
 * Unhandled
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
			//Toolkit.getDefaultToolkit() ;
			ApplicationWrapper wrapper = new ApplicationWrapper();
			wrapper.startApplication();
		} catch (Exception e) {
			log.fatal("A fatal error occurred during startup: " + e);
		}
	}

}
