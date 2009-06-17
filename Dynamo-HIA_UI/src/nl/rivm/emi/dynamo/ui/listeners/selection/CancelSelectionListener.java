package nl.rivm.emi.dynamo.ui.listeners.selection;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CancelSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
	Shell shell;
	boolean deleteFlag = false;

	public CancelSelectionListener(Shell shell) {
		super();
		this.shell = shell;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent selectionEvent) {
		log.info("Control "
				+ ((Control) selectionEvent.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		Control control = ((Control) selectionEvent.getSource());
		Composite parent = control.getParent();
		MessageBox messageBox = new MessageBox(parent.getShell(), SWT.YES
				| SWT.NO);
		messageBox.setText("Leaving edit window");
		messageBox
				.setMessage("Your data will not be saved.\nAre you OK with that?");
		int returnCode = messageBox.open();
		switch (returnCode) {
		case SWT.OK:
			log.debug("MessageBox returns: " + "SWT.OK");
			break;
		case SWT.CANCEL:
			log.debug("MessageBox returns: " + "SWT.CANCEL");
			break;
		case SWT.YES:
			log.debug("MessageBox returns: " + "SWT.YES");
			shell.dispose();
			break;
		case SWT.NO:
			log.debug("MessageBox returns: " + "SWT.NO");
			break;
		case SWT.RETRY:
			log.debug("MessageBox returns: " + "SWT.RETRY");
			break;
		case SWT.ABORT:
			log.debug("MessageBox returns: " + "SWT.ABORT");
			break;
		case SWT.IGNORE:
			log.debug("MessageBox returns: " + "SWT.IGNORE");
			break;
		default:
			log.debug("MessageBox returns something unexpected. Returncode: " + returnCode);
		}
	}

}
