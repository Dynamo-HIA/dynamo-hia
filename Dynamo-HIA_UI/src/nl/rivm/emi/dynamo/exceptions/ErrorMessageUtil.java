package nl.rivm.emi.dynamo.exceptions;

import org.apache.commons.logging.Log;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Handles the error messages, in case the root cause 
 * has to be shown
 * 
 * @author schutb
 *
 */
public class ErrorMessageUtil {
	
	public static void showErrorMessage(Log log, Shell shell,
			Exception e, String messagePrefix, int style) {
		log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(shell, style);
		box.setText(messagePrefix + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();		
	}	
	
}
