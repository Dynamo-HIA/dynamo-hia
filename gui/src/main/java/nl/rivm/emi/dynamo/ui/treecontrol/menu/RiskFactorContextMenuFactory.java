package nl.rivm.emi.dynamo.ui.treecontrol.menu;

/**
 * This Class produces the context-menu for the Node that holds the RiskFactor name.
 * There used to be here, but since all files but the configuration have disapeared 
 * in subdirectories only the management of the configuration-file is left.
 */
import java.util.Collection;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.ui.actions.RiskFactorTypeBulletsAction;
import nl.rivm.emi.dynamo.ui.actions.delete.DeleteDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.delete.MessageStrings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorContextMenuFactory {

	Log log = LogFactory.getLog(this.getClass().getName());

	public void fillRiskFactorContextMenu(Shell shell, TreeViewer treeViewer,
			IMenuManager manager, BaseNode selectedNode)
			throws TreeStructureException {
		ContextMenuEntries contextMenuEntries = initMenuActions(shell,
				treeViewer, selectedNode);
		deActivateConfigurationEntry(selectedNode, contextMenuEntries);
		addActions(manager, contextMenuEntries);
	}

	private void addActions(IMenuManager manager,
			LinkedHashMap<String, ContextMenuEntry> contextMenuEntries) {
		Collection<ContextMenuEntry> entries = contextMenuEntries.values();
		for (ContextMenuEntry entry : entries) {
			manager.add(entry.getMyAction());
		}
	}

	private ContextMenuEntries initMenuActions(Shell shell,
			TreeViewer treeViewer, BaseNode selectedNode) {
		ContextMenuEntries contextMenuEntries = new ContextMenuEntries();
		ContextMenuEntry configurationFileContextMenuEntry = new ContextMenuEntry(
				shell, "Create riskfactor configuration",
				new RiskFactorTypeBulletsAction(shell, treeViewer,
						(DirectoryNode) selectedNode));
		configurationFileContextMenuEntry.setActive(true);
		contextMenuEntries.put(StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
				.getNodeLabel(), configurationFileContextMenuEntry);
		MessageStrings theMessageStrings = new MessageStrings(
				"Removing risk-factor.", "The risk-factor directory ",
				" and\nALL of its contents will be deleted.\nIs that what you want?",
				"The Risk-Factor still has configuration/data files.\n"
						+ "You must delete them all first");
		ContextMenuEntry deleteContextMenuEntry = new ContextMenuEntry(shell,
				"Delete riskfactor", new DeleteDirectoryAction(shell,
						treeViewer, (DirectoryNode) selectedNode, theMessageStrings, 2));
		deleteContextMenuEntry.setActive(true);
		contextMenuEntries.put(StandardTreeNodeLabelsEnum.DELETE
				.getNodeLabel(), deleteContextMenuEntry);
		return contextMenuEntries;
	}

	private void deActivateConfigurationEntry(BaseNode selectedNode,
			ContextMenuEntries contextMenuEntries) {
		Object[] children = ((ParentNode) selectedNode).getChildren();
		for (Object child : children) {
			String childLabel = ((BaseNode) child).deriveNodeLabel();
			if (StandardTreeNodeLabelsEnum.CONFIGURATIONFILE.getNodeLabel()
					.equalsIgnoreCase(childLabel)) {
				ContextMenuEntry entry = contextMenuEntries.get(childLabel);
				entry.setActive(false);
			}
		}
	}
}
