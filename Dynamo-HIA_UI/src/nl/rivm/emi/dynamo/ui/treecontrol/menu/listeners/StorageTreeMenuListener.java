package nl.rivm.emi.dynamo.ui.treecontrol.menu.listeners;

import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.menu.StorageTreeMenuFactory;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuListener implements IMenuListener {

	Shell shell;
	TreeViewer treeViewer;
	StorageTreeMenuFactory stmf; 

	public StorageTreeMenuListener(Shell shell, TreeViewer treeViewer) {
		this.shell = shell;
		this.treeViewer = treeViewer;
		stmf = new StorageTreeMenuFactory(shell, treeViewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface
	 * .action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager manager) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (!selection.isEmpty()) {
			BaseNode selectedNode = (BaseNode) selection.getFirstElement();
			try {
				stmf.createRelevantContextMenu(manager, selection, selectedNode);
			} catch (TreeStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}