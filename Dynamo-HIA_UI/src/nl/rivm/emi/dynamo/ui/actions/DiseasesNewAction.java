package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DiseasesNewAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String selectionPath;
	private Shell shell;
	private TreeViewer theViewer;
	private DirectoryNode node;

	public DiseasesNewAction(Shell shell, TreeViewer v) {
		super();
		this.shell = shell;
		theViewer = v;
	}

	private void setSelectionPath(String selectionPath) {
		this.selectionPath = selectionPath;
	}

	public String getSelectionPath() {
		return selectionPath;
	}

	public void setNode(DirectoryNode node) {
		this.node = node;
		setSelectionPath(node.getPhysicalStorage().getAbsolutePath());
		}

	@Override
	public void run() {
		try {
			MessageBox massageBox = new MessageBox(shell,SWT.ERROR_ITEM_NOT_ADDED);
			massageBox.setMessage("Just a debugBox!");
			massageBox.open();
			InputDialog inputDialog = new InputDialog(shell, "BasePath: "
					+ selectionPath, "Enter name for new disease",
					"Name", /* null */ new FileAndDirectoryNameInputValidator());
			inputDialog.open();
			int returnCode = inputDialog.getReturnCode();
			log.debug("ReturnCode is: " + returnCode);
			if(returnCode != Window.CANCEL){
			String candidateName = inputDialog.getValue();
			String candidatePath = selectionPath + File.separator
					+ candidateName;
			File candidateDirectory = new File(candidatePath);
			if (!candidateDirectory.exists() && candidateDirectory.mkdir()) {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("\"" + candidateName
						+ "\"\nhas been created.");
				messageBox.open();
				node.addChild(new DirectoryNode(node,candidateDirectory));
				theViewer.refresh();
			} else {
				MessageBox messageBox = new MessageBox(shell,SWT.ERROR_ITEM_NOT_ADDED);
				messageBox.setMessage("\"" + candidateName
						+ "\"\ncould not be created.");
				messageBox.open();
			}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("New cancelled.");
				messageBox.open();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
