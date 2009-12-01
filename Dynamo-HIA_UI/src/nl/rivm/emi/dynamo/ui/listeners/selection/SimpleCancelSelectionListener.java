package nl.rivm.emi.dynamo.ui.listeners.selection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SimpleCancelSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());
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
