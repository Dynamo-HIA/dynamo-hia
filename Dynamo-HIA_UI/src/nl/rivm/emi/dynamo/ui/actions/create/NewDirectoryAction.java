package nl.rivm.emi.dynamo.ui.actions.create;

/**
 * Action to create a new directory in the tree.
 * This action can only be called from a directory nodes.
 */
import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewDirectoryAction extends NewActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	/*
	 * Field that contains a possible predefined directoryname. When null the
	 * name should be entered through an InputDialog.
	 */
	String directoryName = null;

	public NewDirectoryAction(Shell shell, TreeViewer v, DirectoryNode node,
			String abstractName, String directoryName) {
		super(shell, v, node, abstractName);
		this.directoryName = directoryName;
	}

	@Override
	public void run() {
		try {
			if (directoryName == null) {
				InputDialog inputDialog = new InputDialog(shell,
						"Create directory below selected one.",
						"Enter name for new " + abstractName, "Name",
						new FileAndDirectoryNameInputValidator());
				inputDialog.open();
				int returnCode = inputDialog.getReturnCode();
				log.fatal("ReturnCode is: " + returnCode);
				if (returnCode != Window.CANCEL) {
					String candidateName = inputDialog.getValue();
					String candidatePath = node.getPhysicalStorage()
							.getAbsolutePath()
							+ File.separator + candidateName;
					handleCreation(candidateName, candidatePath);
				}
				// else {
				// MessageBox messageBox = new MessageBox(shell);
				// messageBox.setMessage("New cancelled.");
				// messageBox.open();
				// }
			} else {
				String candidatePath = node.getPhysicalStorage()
						.getAbsolutePath()
						+ File.separator + directoryName;
				handleCreation(directoryName, candidatePath);
				// MessageBox messageBox = new MessageBox(shell);
				// messageBox.setMessage("Directory: " + directoryName
				// + " created.");
				// messageBox.open();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected void handleCreation(String candidateName, String candidatePath)
			throws StorageTreeException {
		File candidateDirectory = new File(candidatePath);
		if (!candidateDirectory.exists() && candidateDirectory.mkdir()) {
			((ParentNode) node).addChild((ChildNode) new DirectoryNode(
					(ParentNode) node, candidateDirectory));
			theViewer.refresh();
		} else {
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("\"" + candidateName
					+ "\"\ncould not be created.");
			messageBox.open();
		}
	}

}
