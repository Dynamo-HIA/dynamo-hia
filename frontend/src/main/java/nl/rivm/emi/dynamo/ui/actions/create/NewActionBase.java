package nl.rivm.emi.dynamo.ui.actions.create;

import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.StorageTreeException;
import nl.rivm.emi.dynamo.ui.actions.ActionBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

abstract public class NewActionBase extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	protected NewActionBase(Shell shell, TreeViewer v, BaseNode node,
			String abstractName) {
		super(shell, v, node, abstractName);
	}

	abstract protected void handleCreation(String candidateName,
			String candidatePath) throws StorageTreeException;
}
