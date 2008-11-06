package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CancelSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
Shell shell;
	public CancelSelectionListener(Shell shell) {
		super();
		this.shell = shell;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		shell.dispose();
	}

}
