package nl.rivm.emi.dynamo.ui.actions;
/**
 * Action to create a new directory in the tree.
 * This action can only be called from a directory nodes.
 */
import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewDirectoryAction extends NewActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public NewDirectoryAction(Shell shell, TreeViewer v, DirectoryNode node, String abstractName) {
		super(shell, v, node, abstractName);
	}

	protected void handleCreation(String candidateName, String candidatePath)
			throws StorageTreeException {
		File candidateDirectory = new File(candidatePath);
		if (!candidateDirectory.exists() && candidateDirectory.mkdir()) {
//			MessageBox messageBox = new MessageBox(shell);
//			messageBox.setMessage("\"" + candidateName
//					+ "\"\nhas been created.");
//			messageBox.open();
			((ParentNode)node).addChild((ChildNode)new DirectoryNode((ParentNode)node,candidateDirectory));
			theViewer.refresh();
		} else {
			MessageBox messageBox = new MessageBox(shell,SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("\"" + candidateName
					+ "\"\ncould not be created.");
			messageBox.open();
		}
	}

}
