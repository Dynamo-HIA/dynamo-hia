package nl.rivm.emi.dynamo.ui.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import nl.rivm.emi.dynamo.global.BaseNode;

abstract public class ActionBase extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	protected Shell shell;
	protected TreeViewer theViewer;
	protected BaseNode node;
	protected String abstractName;

	protected ActionBase(Shell shell, TreeViewer v, BaseNode node,
			String abstractName) {
		super();
		this.shell = shell;
		theViewer = v;
		this.node = node;
		this.abstractName = abstractName;
	}

	/**
	 * Force implementation on lower levels.
	 */
	abstract public void run();
}
