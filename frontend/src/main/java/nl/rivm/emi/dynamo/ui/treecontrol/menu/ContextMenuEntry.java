package nl.rivm.emi.dynamo.ui.treecontrol.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

public class ContextMenuEntry {
	private Shell myShell;
	private String myMenuText = "Not initialized";
	private Action myAction;
	private boolean active = true;

	public ContextMenuEntry(Shell shell, String menuText, Action action) {
		myShell = shell;
		myMenuText = menuText;
		myAction = action;
	}

	public Action getMyAction() {
		if ((myAction != null) && (myMenuText != null)) {
			myAction.setText(myMenuText);
			myAction.setEnabled(active);
		}
		return myAction;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getMyMenuText() {
		return myMenuText;
	}
}
