package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SimulationNewConfigurationAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	private TreeViewer theViewer;
	private String selectionPath;
	private DirectoryNode simulationsNode;

	public SimulationNewConfigurationAction(Shell shell, TreeViewer v) {
		super();
		this.shell = shell;
		theViewer = v;
	}

	public void setSelectionPath(String selectionPath) {
		this.selectionPath = selectionPath;
	}

	public String getSelectionPath() {
		return selectionPath;
	}

//	public void setShell(Shell shell) {
//		this.shell = shell;
//	}
	
	public void setSimulationsNode(DirectoryNode simulationsNode) {
		this.simulationsNode = simulationsNode;
	}

	@Override
	public void run() {
		try {
			InputDialog inputDialog = new InputDialog(shell, "BasePath: "
					+ selectionPath, "Enter name for new simulation",
					"Simulation name.", null);
			inputDialog.open();
			String candidateSimulationName = inputDialog.getValue();
			String candidateSimulationPath = selectionPath + File.separator
					+ candidateSimulationName;
			File candidateDirectory = new File(candidateSimulationPath);
			MessageBox messageBox = new MessageBox(shell);
			if (!candidateDirectory.exists() && candidateDirectory.mkdir()) {
				messageBox.setMessage(candidateSimulationName
						+ "\nhas been created.");
				simulationsNode.addChild(new DirectoryNode(simulationsNode,candidateDirectory));
				theViewer.refresh();
			} else {
				messageBox.setMessage(candidateSimulationName
						+ "\ncould not be created.");
			}
			messageBox.open();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
