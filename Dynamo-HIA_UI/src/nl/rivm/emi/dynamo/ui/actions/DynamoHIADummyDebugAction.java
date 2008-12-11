package nl.rivm.emi.dynamo.ui.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DynamoHIADummyDebugAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String selectionPath;
	boolean isReferenceData = false;
	boolean isPopulation = false;
	private Shell shell;

	public DynamoHIADummyDebugAction(Shell shell) {
		super();
		this.shell = shell;
	}

	public void setSelectionPath(String selectionPath) {
		this.selectionPath = selectionPath;
	}

	public String getSelectionPath() {
		return selectionPath;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	@Override
	public void run() {
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("Debug");
		box.setMessage("This is a dummy action,\nso it doesn't do anything.");
		box.open();
	}
}
