package nl.rivm.emi.dynamo.ui.listeners.menu;

import java.io.File;

import nl.rivm.emi.dynamo.ui.actions.DynamoHIADummyDebugAction;
import nl.rivm.emi.dynamo.ui.actions.NewDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.PopulationSizeXMLFileAction;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuListener implements IMenuListener {

	Shell shell;
	TreeViewer treeViewer;

	public StorageTreeMenuListener(Shell shell, TreeViewer treeViewer) {
		this.shell = shell;
		this.treeViewer = treeViewer;
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
			createRelevantContextMenu(manager, selection, selectedNode);
		}
	}

	private void createRelevantContextMenu(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode) {
		int treeDepth = findTreeDepth(selectedNode);
		String nodeLabel = deriveNodeLabel(selectedNode);
		switch (treeDepth) {
		case 1:
			// Base directory, no context menu.
			break;
		case 2:
			// Simulations, ReferenceData
			if ("Simulations".equalsIgnoreCase(nodeLabel)) {
				createMenu4Simulations(manager, selection);
			} else {
				if ("Reference_Data".equalsIgnoreCase(nodeLabel)) {
					;
				} else {
					createErrorMenu4UnexpectedNodes(manager, selection,
							treeDepth);
				}

			}
			break;
		case 3:
			// <Simulation-name>, Populations, RiskFactors, Diseases.
			if ("Populations".equalsIgnoreCase(nodeLabel)) {
				createMenu4Populations(manager, selection);
			} else {
				if ("Risk_Factors".equalsIgnoreCase(nodeLabel)) {
					createMenu4RiskFactors(manager, selection);
				} else {
					if ("Diseases".equalsIgnoreCase(nodeLabel)) {
						createMenu4Diseases(manager, selection);
					} else {
						ParentNode parentNode = ((ChildNode) selectedNode)
								.getParent();
						String parentLabel = deriveNodeLabel((BaseNode) parentNode);
						if ("Simulations".equalsIgnoreCase(parentLabel)) {
							createMenu4Simulation(manager, selection);

						} else {
							createErrorMenu4UnexpectedNodes(manager, selection,
									treeDepth);
						}
					}
				}
			}
			break;
		case 4:
			// Modelconfiguration, Parameters, Results, <Scenario-name>,
			// <Population-name>, <RiskFactor-name>,
			// <Disease-name>.
			if ("Modelconfiguration".equalsIgnoreCase(nodeLabel)) {
				createInformationMenu4UnimplementedNodes(manager, selection,
						treeDepth);
			} else {
				if ("Parameters".equalsIgnoreCase(nodeLabel)) {
					createInformationMenu4UnimplementedNodes(manager,
							selection, treeDepth);
				} else {
					if ("Results".equalsIgnoreCase(nodeLabel)) {
						createInformationMenu4UnimplementedNodes(manager,
								selection, treeDepth);
					} else {
						ParentNode parentNode = ((ChildNode) selectedNode)
								.getParent();
						String parentLabel = deriveNodeLabel((BaseNode) parentNode);
						if ("Populations".equalsIgnoreCase(parentLabel)) {
							createMenu4Population(manager, selection);
						} else {
							if ("Risk_Factors".equalsIgnoreCase(parentLabel)) {
								createMenu4RiskFactor(manager, selection);
							} else {
								if ("Diseases".equalsIgnoreCase(parentLabel)) {
									createMenu4Disease(manager, selection);
								} else {
									ParentNode grandParentNode = ((ChildNode) parentNode)
											.getParent();
									String grandParentLabel = deriveNodeLabel((BaseNode) grandParentNode);
									if ("Simulations"
											.equalsIgnoreCase(grandParentLabel)) {
										createMenu4Scenario(manager, selection);
									} else {
										createErrorMenu4UnexpectedNodes(
												manager, selection, treeDepth);
									}
								}
							}
						}
					}
				}
			}
			break;
		case 5:
			if (isXMLFile(selectedNode)) {
				createMenu2EditXML(manager, selection);
			} else {
				ParentNode parentNode = ((ChildNode) selectedNode).getParent();
				ParentNode grandParentNode = ((ChildNode) parentNode)
						.getParent();
				String grandParentLabel = deriveNodeLabel((BaseNode) grandParentNode);
				if ("Risk_Factors".equalsIgnoreCase(grandParentLabel)) {
					createMenu4Transitions(manager, selection);
				} else {
					createDefaultMenu4UnimplementedNodes(manager, selection,
							treeDepth);
				}
			}
			break;
		default:
			createErrorMenu4UnexpectedNodes(manager, selection, treeDepth);
			break;
		}

	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Simulations(final IMenuManager manager,
			IStructuredSelection selection) {
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				null, "simulation");
		sNAction.setText("New simulation");
		manager.add(sNAction);
	}

	/**
	 * TODO
	 * 
	 * @param mgr
	 * @param selection
	 */
	private void createMenu4Simulation(final IMenuManager mgr,
			IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: New configuration");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		mgr.add(action);
	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Populations(IMenuManager manager,
			IStructuredSelection selection) {
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "population");
		sNAction.setText("New population");
		manager.add(sNAction);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Population(IMenuManager manager,
			IStructuredSelection selection) {
		PopulationSizeXMLFileAction action = new PopulationSizeXMLFileAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				"populationsize");
		action.setText("New populationsize");
		manager.add(action);
		DynamoHIADummyDebugAction action2 = new DynamoHIADummyDebugAction(shell);
		action2.setText("Dummy2: Population");
		action2.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action2);
		DynamoHIADummyDebugAction action3 = new DynamoHIADummyDebugAction(shell);
		action3.setText("Dummy3: Population");
		action3.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action3);
		DynamoHIADummyDebugAction action4 = new DynamoHIADummyDebugAction(shell);
		action4.setText("Dummy4: Population");
		action4.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action4);
	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactors(IMenuManager manager,
			IStructuredSelection selection) {
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "riskfactor");
		sNAction.setText("New riskfactor");
		manager.add(sNAction);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactor(IMenuManager manager,
			IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: Risk factor");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Diseases(IMenuManager manager,
			IStructuredSelection selection) {
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "disease");
		sNAction.setText("New disease");
		manager.add(sNAction);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Disease(IMenuManager manager,
			IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: Disease");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Transitions(IMenuManager manager,
			IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: Transitions");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * TODO Precondition: The selected Node maps to an XML file.
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu2EditXML(IMenuManager manager,
			IStructuredSelection selection) {
		FileNode node = (FileNode) selection.getFirstElement();
		ParentNode parentNode = node.getParent();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		String nodeLabel = deriveNodeLabel(node);
		if ("populations"
				.equalsIgnoreCase(deriveNodeLabel((BaseNode) grandParentNode))) {
			PopulationSizeXMLFileAction action = new PopulationSizeXMLFileAction(
					shell, treeViewer, (BaseNode) node, "populationsize");
			manager.add(action);
		} else {
			DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(
					shell);
			action.setText("Dummy: Edit XML.");
			action.setSelectionPath(((BaseNode) selection.getFirstElement())
					.getPhysicalStorage().getAbsolutePath());
			manager.add(action);
		}
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Scenario(IMenuManager manager,
			IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: Scenario");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * Default menu to indicate the menu for this Node has not yet been
	 * implemented.
	 * 
	 * @param manager
	 * @param selection
	 * @param treeDepth
	 */
	private void createDefaultMenu4UnimplementedNodes(
			final IMenuManager manager, IStructuredSelection selection,
			int treeDepth) {
		String selectionPath = ((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath();
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Not yet implemented: " + selectionPath);
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * Error menu to indicate the choice of this Node was unexpected.
	 * 
	 * @param manager
	 * @param selection
	 * @param treeDepth
	 */
	private void createErrorMenu4UnexpectedNodes(final IMenuManager manager,
			IStructuredSelection selection, int treeDepth) {
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		// String selectionPath = selectedNode.getPhysicalStorage()
		// .getAbsolutePath();
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Error: Unexpected nodename \""
				+ deriveNodeLabel(selectedNode) + "\" at level " + treeDepth);
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * Information menu to indicate this Node and all nodes below it will not be
	 * managed by Dynamo-HIA.
	 * 
	 * @param manager
	 * @param selection
	 * @param treeDepth
	 */
	private void createInformationMenu4UnimplementedNodes(
			final IMenuManager manager, IStructuredSelection selection,
			int treeDepth) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Info: This node will not be managed by this software.");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	private int findTreeDepth(BaseNode selectedNode) {
		int depth = 0;
		BaseNode workNode = selectedNode;
		do {
			workNode = (BaseNode) ((ChildNode) workNode).getParent();
			depth++;
		} while (!(workNode instanceof RootNode));
		return depth;
	}

	/**
	 * Returns the name of the directory/file represented by the Node minus any
	 * extensions.
	 * 
	 * @param selectedNode
	 * @return
	 */
	private String deriveNodeLabel(BaseNode selectedNode) {
		String physicalStorageName = selectedNode.getPhysicalStorage()
				.getName();
		int firstDotIndex = -1;
		if ((physicalStorageName != null)
				&& ((firstDotIndex = physicalStorageName.indexOf(".")) != -1)) {
			physicalStorageName = physicalStorageName.substring(firstDotIndex);
		}
		return physicalStorageName;
	}

	/**
	 * Returns the name of the directory/file represented by the Node minus any
	 * extensions.
	 * 
	 * @param selectedNode
	 * @return
	 */
	private boolean isXMLFile(BaseNode selectedNode) {
		boolean result = false;
		File physicalStorage = selectedNode.getPhysicalStorage();
		if (physicalStorage.isFile()) {
			String physicalStorageName = physicalStorage.getName();
			int lastDotIndex = -1;
			if ((physicalStorageName != null)
					&& ((lastDotIndex = physicalStorageName.lastIndexOf(".")) != -1)
					&& (lastDotIndex < physicalStorage.length())) {
				String extension = physicalStorageName.substring(
						lastDotIndex + 1, physicalStorageName.length());
				if ("xml".equalsIgnoreCase(extension)) {
					result = true;
				}
			}
		}
		return result;
	}
}