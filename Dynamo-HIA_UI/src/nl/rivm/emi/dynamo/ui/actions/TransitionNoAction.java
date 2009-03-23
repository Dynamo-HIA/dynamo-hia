package nl.rivm.emi.dynamo.ui.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Handles Transition files that do not need any further action: 
 * transitiondrift_netto, transitionmatrix_zero, transitionmatrix_netto 
 * 
 * @author schutb
 *
 */
public class TransitionNoAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String selectionPath;
	boolean isReferenceData = false;
	boolean isPopulation = false;
	private Shell shell;

	public TransitionNoAction(Shell shell) {
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
		// No Action needed
	}
}
