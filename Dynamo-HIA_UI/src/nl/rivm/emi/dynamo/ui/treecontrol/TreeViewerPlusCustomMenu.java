package nl.rivm.emi.dynamo.ui.treecontrol;

import nl.rivm.emi.dynamo.ui.listeners.menu.StorageTreeMenuListener;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Shell;

public class TreeViewerPlusCustomMenu {

	public TreeViewerPlusCustomMenu(Shell shell,
			StorageTreeContentProvider contentProvider) {
		final TreeViewer treeViewer = new TreeViewer(shell);
		treeViewer.setLabelProvider(new LabelProvider());
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setInput(contentProvider.rootNode);
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new StorageTreeMenuListener(shell, treeViewer));
		treeViewer.getControl().setMenu(mgr.createContextMenu(treeViewer.getControl()));
	}
}
