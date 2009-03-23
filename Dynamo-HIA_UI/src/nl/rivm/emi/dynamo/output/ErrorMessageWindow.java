/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author boshuizh
 *
 */
public class ErrorMessageWindow {
	/**
	 * @param e1
	 * @param outputShell
	 */
	

	public ErrorMessageWindow(final Exception e, final Shell parentShell) {

		Shell shell = new Shell(parentShell);

		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("error while calculating output."
						+ " Message given: " + e.getMessage()
						+ ". ");
		e.printStackTrace();
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();

	}

	/**
	 * @param e1
	 * @param outputShell
	 */
	public ErrorMessageWindow(FactoryConfigurationError e1, Shell parentShell) {
		
		Shell shell = new Shell(parentShell);

		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("error while calculating output."
						+ " Message given: " + e1.getMessage()
						+ ". ");
		
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();

	}

	/**
	 * @param e1
	 * @param outputShell
	 */
	public ErrorMessageWindow(FileNotFoundException e1, Shell parentShell) {
		Shell shell = new Shell(parentShell);

		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("error while calculating output."
						+ " Message given: " + e1.getMessage()
						+ ". ");
		
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();
	
	}
}
