/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *******************************************************************************/

package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

import nl.rivm.emi.dynamo.ui.actions.DynamoHIATreeAction;
import nl.rivm.emi.dynamo.ui.actions.SimulationsNewAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.MessageBox;
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
