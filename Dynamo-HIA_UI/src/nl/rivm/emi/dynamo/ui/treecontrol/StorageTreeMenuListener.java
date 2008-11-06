package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

import nl.rivm.emi.dynamo.ui.actions.DynamoHIATreeAction;
import nl.rivm.emi.dynamo.ui.actions.SimulationsNewAction;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuListener implements IMenuListener {

	// Shell shell;
	TreeViewer treeViewer;
	final SimulationsNewAction sNAction;
	final DynamoHIATreeAction action;

	public StorageTreeMenuListener(Shell shell, TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		sNAction = new SimulationsNewAction(shell, treeViewer);
		action = new DynamoHIATreeAction(shell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager manager) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (!selection.isEmpty()) {
			String absoluteSelectionPath = ((BaseNode) selection
					.getFirstElement()).getPhysicalStorage().getAbsolutePath();
			String selectionName = absoluteSelectionPath.substring(
					absoluteSelectionPath.lastIndexOf(File.separatorChar) + 1,
					absoluteSelectionPath.length());
			if ("simulations".equalsIgnoreCase(selectionName)) {
				createMenu4Simulations(sNAction, manager, selection);
			} else {
				// action.setText("Action for "
				// + ((BaseNode) selection.getFirstElement())
				// .getPhysicalStorage().getAbsolutePath());
				action.setText(selectionName);
				action
						.setSelectionPath(((BaseNode) selection
								.getFirstElement()).getPhysicalStorage()
								.getAbsolutePath());
				manager.add(action);
			}
		}
	}

	private void createMenu4Simulations(final SimulationsNewAction sNAction,
			final IMenuManager mgr, IStructuredSelection selection) {
		sNAction.setText("New simulation");
		sNAction.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		sNAction
				.setSimulationsNode((DirectoryNode) selection.getFirstElement());
		mgr.add(sNAction);
	}
}
