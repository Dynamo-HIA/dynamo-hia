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

import nl.rivm.emi.dynamo.ui.actions.DynamoHIATreeAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class TreeViewerPlusCustomMenu {

	public TreeViewerPlusCustomMenu(Shell shell,
			StorageTreeContentProvider contentProvider) {
		final TreeViewer v = new TreeViewer(shell);
		v.setLabelProvider(new LabelProvider());
		v.setContentProvider(contentProvider);
		v.setInput(contentProvider.rootNode);

		final DynamoHIATreeAction action = new DynamoHIATreeAction(
				shell);
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) v
						.getSelection();
				if (!selection.isEmpty()) {
					action.setText("Action for "
							+ ((BaseNode) selection.getFirstElement())
									.getPhysicalStorage().getAbsolutePath());
					action.setSelectionPath(((BaseNode) selection
							.getFirstElement()).getPhysicalStorage()
							.getAbsolutePath());
					mgr.add(action);
				}
			}
		});
		v.getControl().setMenu(mgr.createContextMenu(v.getControl()));
	}

}
