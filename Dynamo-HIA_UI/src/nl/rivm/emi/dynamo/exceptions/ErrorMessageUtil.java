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
	
	/**
	 * @param log log handler
	 * @param shell the shell on which the message box is projected
	 * @param e the exception to be shown
	 * @param messagePrefix Prefix of the message to be shown
	 * @param style the SWT style in which the message is shown
	 */
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
