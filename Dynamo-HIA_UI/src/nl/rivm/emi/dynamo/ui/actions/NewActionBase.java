package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

abstract public class NewActionBase extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	protected NewActionBase(Shell shell, TreeViewer v, DirectoryNode node,
			String abstractName) {
		super(shell, v, node, abstractName);
	}

	abstract protected void handleCreation(String candidateName,
			String candidatePath) throws StorageTreeException;
}
