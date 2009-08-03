package nl.rivm.emi.dynamo.ui.listeners.selection;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SimpleCancelSelectionListener extends AbstractLoggingClass
		implements SelectionListener {
	Shell shell2Handle;

	public SimpleCancelSelectionListener(Shell shell) {
		super();
		shell2Handle = shell;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent selectionEvent) {
		shell2Handle.dispose();
	}
}
