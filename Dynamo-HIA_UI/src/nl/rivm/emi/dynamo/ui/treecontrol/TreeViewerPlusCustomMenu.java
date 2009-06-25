package nl.rivm.emi.dynamo.ui.treecontrol;

import nl.rivm.emi.dynamo.ui.treecontrol.menu.listeners.StorageTreeMenuListener;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Shell;

public class TreeViewerPlusCustomMenu {

	private static TreeViewer treeViewer = null; 
	
	public TreeViewerPlusCustomMenu(Shell shell,
			StorageTreeContentProvider contentProvider) {
		treeViewer = new TreeViewer(shell);
		treeViewer.setLabelProvider(new FileTreeLabelProvider());
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setInput(contentProvider.rootNode);
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new StorageTreeMenuListener(shell, treeViewer));
		treeViewer.getControl().setMenu(mgr.createContextMenu(treeViewer.getControl()));
		// Open the tree at startup upto simulation-level.
		treeViewer.expandToLevel(3);
	}
	
	/**
	 * @return TreeViewer
	 */
	public static TreeViewer getTreeViewerInstance() {
		return treeViewer;
	}
}
