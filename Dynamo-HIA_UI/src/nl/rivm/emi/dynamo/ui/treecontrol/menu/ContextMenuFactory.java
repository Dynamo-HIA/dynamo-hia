package nl.rivm.emi.dynamo.ui.treecontrol.menu;

import java.util.Collection;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.ui.actions.NewDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.NewRelRisksForDeathAction;
import nl.rivm.emi.dynamo.ui.actions.NewRelRisksForDisabilityAction;
import nl.rivm.emi.dynamo.ui.actions.NewRiskFactorPrevalencesAction;
import nl.rivm.emi.dynamo.ui.actions.RiskFactorTypeBulletsAction;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class ContextMenuFactory {

	Log log = LogFactory.getLog(this.getClass().getName());

	public void fillRiskFactorContextMenu(Shell shell, TreeViewer treeViewer,
			IMenuManager manager, BaseNode selectedNode)
			throws TreeStructureException {
		ContextMenuEntries contextMenuEntries = initMenuActions(shell,
				treeViewer, selectedNode);
		boolean noConfig = true;
		Object[] children = ((ParentNode) selectedNode).getChildren();
		for (Object child : children) {
			String childLabel = ((BaseNode) child).deriveNodeLabel();
			ContextMenuEntry entry = contextMenuEntries.get(childLabel);
			if (entry == null) {
				log.fatal("No menu entry found for child: " + childLabel);
			} else {
				if (StandardTreeNodeLabelsEnum.CONFIGURATIONFILE.getNodeLabel()
						.equals(childLabel)) {
					noConfig = false;
					entry.setActive(false);
				} else {
					entry.setActive(false);
				}
			}
		}
		if (noConfig) {
			contextMenuEntries.setNoConfig();
		}
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
		contextMenuEntries.put(StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
				.getNodeLabel(), configurationFileContextMenuEntry);
		ContextMenuEntry transitionDirectoryContextMenuEntry = new ContextMenuEntry(
				shell, "Create transition directory", new NewDirectoryAction(
						shell, treeViewer, (DirectoryNode) selectedNode, "transition",
						null));
		contextMenuEntries.put(StandardTreeNodeLabelsEnum.TRANSITION
				.getNodeLabel(), transitionDirectoryContextMenuEntry);
//		ContextMenuEntry prevalenceFileContextMenuEntry = new ContextMenuEntry(
//				shell, "Create prevalences configuration",
//				new NewRiskFactorPrevalencesAction(shell, treeViewer, selectedNode));
//		contextMenuEntries.put(StandardTreeNodeLabelsEnum.PREVALENCEFILE
//				.getNodeLabel(), prevalenceFileContextMenuEntry);
		ContextMenuEntry relRiskForDeathFileContextMenuEntry = new ContextMenuEntry(
				shell, "Create relative risks for death configuration",
				new NewRelRisksForDeathAction(shell, treeViewer, selectedNode));
		contextMenuEntries.put(StandardTreeNodeLabelsEnum.RELRISKFORDEATHFILE
				.getNodeLabel(), relRiskForDeathFileContextMenuEntry);
		ContextMenuEntry relRiskForDisabilityFileContextMenuEntry = new ContextMenuEntry(
				shell, "Create relative risks for disability configuration",
				new NewRelRisksForDisabilityAction(shell, treeViewer, selectedNode));
		contextMenuEntries.put(
				StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYFILE
						.getNodeLabel(),
				relRiskForDisabilityFileContextMenuEntry);
		return contextMenuEntries;
	}

	static private class ContextMenuEntries extends
			LinkedHashMap<String, ContextMenuEntry> {

		public void setNoConfig() {
			Collection<ContextMenuEntry> entries = values();
			for (ContextMenuEntry currentEntry : entries) {
				if (!currentEntry
						.equals(get(StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
								.getNodeLabel()))) {
					currentEntry.setActive(false);
				}
			}
		}
	}

	static private class ContextMenuEntry {
		private Shell myShell;
		private String myMenuText = "Not initialized";
		private Action myAction;
		private boolean active = true;

		public ContextMenuEntry(Shell shell, String menuText, Action action) {
			myShell = shell;
			myMenuText = menuText;
			myAction = action;
		}

		public Action getMyAction() {
			if ((myAction != null) && (myMenuText != null)) {
				myAction.setText(myMenuText);
				myAction.setEnabled(active);
			}
			return myAction;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getMyMenuText() {
			return myMenuText;
		}
	}
}
