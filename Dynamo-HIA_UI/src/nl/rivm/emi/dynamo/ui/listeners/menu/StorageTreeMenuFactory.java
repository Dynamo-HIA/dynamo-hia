package nl.rivm.emi.dynamo.ui.listeners.menu;

import nl.rivm.emi.dynamo.ui.actions.DynamoHIADummyDebugAction;
import nl.rivm.emi.dynamo.ui.actions.FreeNameXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.NewDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.OverallDALYWeightsXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.OverallMortalityXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.PopulationSizeXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.main.OverallMortalityModal;
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

public class StorageTreeMenuFactory {

	Shell shell;
	TreeViewer treeViewer;

	public StorageTreeMenuFactory(Shell shell, TreeViewer treeViewer) {
		this.shell = shell;
		this.treeViewer = treeViewer;
	}

	public void createRelevantContextMenu(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode) {
		int treeDepth = findTreeDepth(selectedNode);
		String nodeLabel = selectedNode.deriveNodeLabel();
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
						String parentLabel = ((BaseNode) parentNode)
								.deriveNodeLabel();
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
						String parentLabel = ((BaseNode) parentNode)
								.deriveNodeLabel();
						if ("Populations".equalsIgnoreCase(parentLabel)) {
							createMenu4Population(manager, selection);
						} else {
							if ("Risk_Factors".equalsIgnoreCase(parentLabel)) {
								createMenu4RiskFactor(manager, selection);
							} else {
								if ("Diseases".equalsIgnoreCase(parentLabel)) {
									// createMenu4Disease(manager, selection);
								} else {
									ParentNode grandParentNode = ((ChildNode) parentNode)
											.getParent();
									String grandParentLabel = ((BaseNode) grandParentNode)
											.deriveNodeLabel();
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
			if (selectedNode.isXMLFile()) {
				createMenu2EditXML(manager, selection);
			} else {
				ParentNode parentNode = ((ChildNode) selectedNode).getParent();
				ParentNode grandParentNode = ((ChildNode) parentNode)
						.getParent();
				String grandParentLabel = ((BaseNode) grandParentNode)
						.deriveNodeLabel();
				if ("Risk_Factors".equalsIgnoreCase(grandParentLabel)) {
					createMenu4Transitions(manager, selection);
				} else {
					if ("Diseases".equalsIgnoreCase(grandParentLabel)) {
						if ("Incidences".equalsIgnoreCase(nodeLabel)) {
							createMenu4Incidences(manager, selection);
						} else {
							if ("Prevalences".equalsIgnoreCase(nodeLabel)) {
								createMenu4Prevalence(manager, selection);
							} else {
								createDefaultMenu4UnimplementedNodes(manager,
										selection, treeDepth);
							}
						}
					} else {
						createDefaultMenu4UnimplementedNodes(manager,
								selection, treeDepth);
					}
				}
			}
			break;
		case 6:
			if (selectedNode.isXMLFile()) {
				createMenu2EditXML(manager, selection);
			} else {
				createDefaultMenu4UnimplementedNodes(manager, selection,
						treeDepth);
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
	private void createMenu4Simulation(final IMenuManager manager,
			IStructuredSelection selection) {
		XMLFileAction action = new XMLFileAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "configuration",
				"simulation");
		action.setText("New configuration");
		manager.add(action);
	}

	/**
	 * TODO
	 * 
	 * @param mgr
	 * @param selection
	 */
	private void createMenu4SimulationConfiguration(final IMenuManager manager,
			IStructuredSelection selection) {
		XMLFileAction action = new XMLFileAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "configuration",
				"simulation");
		action.setText("New configuration");
		manager.add(action);
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
		XMLFileAction action = new XMLFileAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "size",
				"populationsize");
		action.setText("New populationsize");
		manager.add(action);
		XMLFileAction action2 = new XMLFileAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overallmortality", "overallmortality");
		action2.setText("New overall mortality");
		manager.add(action2);
		XMLFileAction action3 = new XMLFileAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overalldalyweights", "overalldalyweights");
		action3.setText("New overall DALY weights");
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
	 * No action.
	 * 
	 * @param manager
	 * @param selection
	 */
	// private void createMenu4Disease(IMenuManager manager,
	// IStructuredSelection selection) {
	// DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
	// action.setText("Dummy: Disease");
	// action.setSelectionPath(((BaseNode) selection.getFirstElement())
	// .getPhysicalStorage().getAbsolutePath());
	// manager.add(action);
	// }
	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Prevalence(IMenuManager manager,
			IStructuredSelection selection) {
		FreeNameXMLFileAction action = new FreeNameXMLFileAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				"diseaseprevalences");
		action.setText("New disease prevalences file");
		manager.add(action);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Incidences(IMenuManager manager,
			IStructuredSelection selection) {
		FreeNameXMLFileAction action = new FreeNameXMLFileAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				"diseaseincidences");
		action.setText("New disease incidences file");
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
		String nodeLabel = node.toString();
		ParentNode parentNode = node.getParent();
		String parentNodeLabel = parentNode.toString();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		if ("populations".equalsIgnoreCase(((BaseNode) grandParentNode)
				.deriveNodeLabel())) {
			handlePopulations(manager, selection, node, nodeLabel);
		} else {
			if ("simulations".equalsIgnoreCase(((BaseNode) grandParentNode)
					.deriveNodeLabel())) {
				XMLFileAction action = new XMLFileAction(shell, treeViewer,
						(BaseNode) node, "simulation", nodeLabel);
				action.setText("Edit");
				manager.add(action);
			} else {
				if ("incidences".equalsIgnoreCase(parentNodeLabel)) {
					XMLFileAction action = new XMLFileAction(shell, treeViewer,
							(BaseNode) node, node.toString(),
							"diseaseincidences");
					action.setText("Edit");
					manager.add(action);
				} else {
					if ("prevalences".equalsIgnoreCase(parentNodeLabel)) {
						XMLFileAction action = new XMLFileAction(shell,
								treeViewer, (BaseNode) node, node.toString(),
								"diseaseprevalences");
						action.setText("Edit");
						manager.add(action);
					} else {
						DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(
								shell);
						action.setText("Dummy: Edit XML.");
						action.setSelectionPath(((BaseNode) selection
								.getFirstElement()).getPhysicalStorage()
								.getAbsolutePath());
						manager.add(action);
					}
				}
			}
		}
	}

	private void handlePopulations(IMenuManager manager,
			IStructuredSelection selection, FileNode node, String nodeLabel) {
		if ("size".equals(nodeLabel)) {
			PopulationSizeXMLFileAction action = new PopulationSizeXMLFileAction(
					shell, treeViewer, (BaseNode) node, "populationsize");
			action.setText("Edit");
			manager.add(action);
		} else {
			if ("overallmortality".equals(nodeLabel)) {
				OverallMortalityXMLFileAction action = new OverallMortalityXMLFileAction(
						shell, treeViewer, (BaseNode) node, "overallmortality");
				action.setText("Edit");
				manager.add(action);
			} else {
				if ("overalldalyweights".equals(nodeLabel)) {
					OverallDALYWeightsXMLFileAction action = new OverallDALYWeightsXMLFileAction(
							shell, treeViewer, (BaseNode) node,
							"overalldalyweights");
					action.setText("Edit");
					manager.add(action);
				} else {
					DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(
							shell);
					action.setText("Dummy: Edit XML.");
					action.setSelectionPath(((BaseNode) selection
							.getFirstElement()).getPhysicalStorage()
							.getAbsolutePath());
					manager.add(action);
				}
			}

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
				+ selectedNode.deriveNodeLabel() + "\" at level " + treeDepth);
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

}