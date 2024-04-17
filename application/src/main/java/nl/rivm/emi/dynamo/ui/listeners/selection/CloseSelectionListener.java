package nl.rivm.emi.dynamo.ui.listeners.selection;

import nl.rivm.emi.dynamo.ui.main.base.DataAndFileContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

public class CloseSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	DataAndFileContainer myModal;

	public CloseSelectionListener(DataAndFileContainer theModal) {
		super();
		myModal = theModal;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent selectionEvent) {
		log.info("Control "
				+ ((Control) selectionEvent.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		// 20091110 RLM Don't bother a user if nothing can be saved.
		if (myModal.isChanged()) {
			if (!myModal.isConfigurationFileReadOnly()) {
				Control control = ((Control) selectionEvent.getSource());
				Composite parent = control.getParent();
				MessageBox messageBox = new MessageBox(parent.getShell(),
						SWT.YES | SWT.NO);
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
					myModal.getShell().dispose();
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
					log
							.debug("MessageBox returns something unexpected. Returncode: "
									+ returnCode);
				}
			} else {
				Control control = ((Control) selectionEvent.getSource());
				Composite parent = control.getParent();
				MessageBox messageBox = new MessageBox(parent.getShell(),
						SWT.OK);
				messageBox.setText("Leaving edit window");
				messageBox
						.setMessage("You have changed a readonly configuration\n This configuration cannot be saved.");
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
					log
							.debug("MessageBox returns something unexpected. Returncode: "
									+ returnCode);
				}
				myModal.getShell().dispose();
			}
		} else {
			myModal.getShell().dispose();
		}
	}
}
