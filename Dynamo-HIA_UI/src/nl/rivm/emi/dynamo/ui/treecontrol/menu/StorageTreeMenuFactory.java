package nl.rivm.emi.dynamo.ui.treecontrol.menu;

/**
 * TODO Get rootelement-names from the FileControlEnum.
 * 
 */
import java.io.File;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.ui.actions.DynamoHIADummyDebugAction;
import nl.rivm.emi.dynamo.ui.actions.FreeNameXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.InputBulletsFreeXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.NewDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.OverallDALYWeightsXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.OverallMortalityXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.PopulationSizeXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.RelativeRiskFromRiskSourceAction;
import nl.rivm.emi.dynamo.ui.actions.TransitionNoAction;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuFactory {

	Log log = LogFactory.getLog(this.getClass().getName());
	
	Shell shell;
	TreeViewer treeViewer;
	ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	public StorageTreeMenuFactory(Shell shell, TreeViewer treeViewer) {
		this.shell = shell;
		this.treeViewer = treeViewer;
	}

	public void createRelevantContextMenu(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode)
			throws TreeStructureException, ConfigurationException {
		int treeDepth = findTreeDepth(selectedNode);
		log.debug("treeDepth" + treeDepth);
		
		String nodeLabel = selectedNode.deriveNodeLabel();
		switch (treeDepth) {
		case 1:
			// Base directory, no context menu.
			break;
		case 2:
			// Simulations, ReferenceData
			if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createMenu4Simulations(manager, selection);
			} else {
				if (StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel()
						.equalsIgnoreCase(nodeLabel)) {
					;
				} else {
					createErrorMenu4UnexpectedNodes(manager, selection,
							treeDepth);
				}
			}
			break;
		case 3:
			// <Simulation-name>, Populations, RiskFactors, Diseases.
			if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createMenu4Populations(manager, selection);
			} else {
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
						.equalsIgnoreCase(nodeLabel)) {
					createMenu4RiskFactors(manager, selection);
				} else {
					if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
							.equalsIgnoreCase(nodeLabel)) {
						createMenu4Diseases(manager, selection);
					} else {
						ParentNode parentNode = ((ChildNode) selectedNode)
								.getParent();
						String parentLabel = ((BaseNode) parentNode)
								.deriveNodeLabel();
						if (StandardTreeNodeLabelsEnum.SIMULATIONS
								.getNodeLabel().equalsIgnoreCase(parentLabel)) {
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
			if (StandardTreeNodeLabelsEnum.MODELCONFIGURATION.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createInformationMenu4UnimplementedNodes(manager, selection,
						treeDepth);
			} else {
				if (StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel()
						.equalsIgnoreCase(nodeLabel)) {
					createInformationMenu4UnimplementedNodes(manager,
							selection, treeDepth);
				} else {
					if (StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel()
							.equalsIgnoreCase(nodeLabel)) {
						createInformationMenu4UnimplementedNodes(manager,
								selection, treeDepth);
					} else {
						ParentNode parentNode = ((ChildNode) selectedNode)
								.getParent();
						String parentLabel = ((BaseNode) parentNode)
								.deriveNodeLabel();
						if (StandardTreeNodeLabelsEnum.POPULATIONS
								.getNodeLabel().equalsIgnoreCase(parentLabel)) {
							createMenu4Population(manager, selection);
						} else {
							if (StandardTreeNodeLabelsEnum.RISKFACTORS
									.getNodeLabel().equalsIgnoreCase(
											parentLabel)) {
								createMenu4RiskFactor(manager, selection);
							} else {
								if (StandardTreeNodeLabelsEnum.DISEASES
										.getNodeLabel().equalsIgnoreCase(
												parentLabel)) {
									// createMenu4Disease(manager, selection);
								} else {
									ParentNode grandParentNode = ((ChildNode) parentNode)
											.getParent();
									String grandParentLabel = ((BaseNode) grandParentNode)
											.deriveNodeLabel();
									if (StandardTreeNodeLabelsEnum.SIMULATIONS
											.getNodeLabel().equalsIgnoreCase(
													grandParentLabel)) {
										createMenu2EditXML(manager, selection);
										//createMenu4Scenario(manager, selection);
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
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
						.equalsIgnoreCase(grandParentLabel)) {
					createMenu4Transitions(manager, selection);
				} else {
					if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
							.equalsIgnoreCase(grandParentLabel)) {
						if (StandardTreeNodeLabelsEnum.DALYWEIGHTS
								.getNodeLabel().equalsIgnoreCase(
										nodeLabel)) {
							createMenu4DALY_Weights(manager, selection);
						} else {
							if (StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
									.getNodeLabel().equalsIgnoreCase(
											nodeLabel)) {
								createMenu4Excess_Mortalities(manager, selection);
							} else {
						if (StandardTreeNodeLabelsEnum.INCIDENCES
								.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
							createMenu4Incidences(manager, selection);
						} else {
							if (StandardTreeNodeLabelsEnum.PREVALENCES
									.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
								createMenu4Prevalence(manager, selection);
							} else {
									if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
											.getNodeLabel().equalsIgnoreCase(
													nodeLabel)) {
										createMenu4RelRiskFromDiseases(manager,
												selection);
									} else {
										if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
												.getNodeLabel()
												.equalsIgnoreCase(nodeLabel)) {
											createMenu4RelRiskFromRiskFactor(
													manager, selection);
										} else {
											/*
											createDefaultMenu4UnimplementedNodes(
													manager, selection,
													treeDepth);
											*/								
											
											ErrorMessageUtil.showErrorMessage(log, shell, new DynamoConfigurationException("Not implemented yet"), 
											"", SWT.ERROR_UNSPECIFIED);				
											
										}
									}
								}
							}
							}
							}
					} else {
						ErrorMessageUtil.showErrorMessage(log, shell, new DynamoConfigurationException("Not implemented yet"), 
								"", SWT.ERROR_UNSPECIFIED);
						/*
						createDefaultMenu4UnimplementedNodes(manager,
								selection, treeDepth);
								*/
					}
				}
			}
			break;
		case 6:
			if (selectedNode.isXMLFile()) {
				createMenu2EditXML(manager, selection);
			} else {
				/*
				createDefaultMenu4UnimplementedNodes(manager, selection,
						treeDepth);
				*/				
				ErrorMessageUtil.showErrorMessage(log, shell, new DynamoConfigurationException("No xml file found"), 
						"", SWT.ERROR_UNSPECIFIED);				
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
				(DirectoryNode) selectedNode, "simulation", null);
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
				RootElementNamesEnum.SIMULATION.getNodeLabel());
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
				(DirectoryNode) selection.getFirstElement(), "population", null);
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
		
		// Calling screen W11
		XMLFileAction action = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(), "size",
				"populationsize");
		action.setText("New populationsize");
		manager.add(action);

		// Calling screen W12
		XMLFileAction action2 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overallmortality", "overallmortality");
		action2.setText("New overall mortality");
		manager.add(action2);
		
		// Calling screen W13
		XMLFileAction action3 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"newborns", "newborns");
		action3.setText("New newborns");
		manager.add(action3);
		
		// Calling screen W14
		XMLFileAction action4 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overalldalyweights", "overalldalyweights");
		action4.setText("New overall DALY weights");
		manager.add(action4);
		DynamoHIADummyDebugAction action5 = new DynamoHIADummyDebugAction(this.shell);
		action5.setText("Dummy4: Population");
		action5.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action5);
	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactors(IMenuManager manager,
			IStructuredSelection selection) {
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "riskfactor", null);
		sNAction.setText("New riskfactor");
		manager.add(sNAction);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 * @throws TreeStructureException
	 */
	private void createMenu4RiskFactor(IMenuManager manager,
			IStructuredSelection selection) throws TreeStructureException {
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		contextMenuFactory.fillRiskFactorContextMenu(shell, treeViewer,
				manager, selectedNode);
	}

	/**
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Diseases(IMenuManager manager,
			IStructuredSelection selection) {
		NewDirectoryAction sNAction = new NewDirectoryAction(shell, treeViewer,
				(DirectoryNode) selection.getFirstElement(), "disease", null);
		sNAction.setText("New disease");
		manager.add(sNAction);
	}

	/**
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

	private void createMenu4DALY_Weights(IMenuManager manager,
			IStructuredSelection selection) {
		FreeNameXMLFileAction action = new FreeNameXMLFileAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				"dalyweights");
		action.setText("New disease DALY weights file");
		manager.add(action);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */

	private void createMenu4Excess_Mortalities(IMenuManager manager,
			IStructuredSelection selection) {
		FreeNameXMLFileAction action = new FreeNameXMLFileAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel());
		action.setText("New disease excess mortalities file");
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
	private void createMenu4RelRiskFromDiseases(IMenuManager manager,
			IStructuredSelection selection) {
		RelativeRiskFromRiskSourceAction action = new RelativeRiskFromRiskSourceAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				RootElementNamesEnum.RELATIVERISKSFROMDISEASES.getNodeLabel());
		action.setText("New relative risks from other disease file");
		manager.add(action);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RelRiskFromRiskFactor(IMenuManager manager,
			IStructuredSelection selection) {
		RelativeRiskFromRiskSourceAction action = new RelativeRiskFromRiskSourceAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				null);
		action.setText("New relative risks from risk factor file");
		manager.add(action);
	}

	/**
	 * TODO implement create new transitions file
	 * xxxxxxxxxxxxxxxxxxxxxxsss
	 * @param manager
	 * @param selection
	 * @throws ConfigurationException 
	 */
	private void createMenu4Transitions(IMenuManager manager,
			IStructuredSelection selection) throws ConfigurationException {		

		
		
		File configurationFile = null;
		
		DirectoryNode fileNode = (DirectoryNode) selection.getFirstElement();
		
		
		Object[] grandChildNodes = ((ParentNode) fileNode.getParent()).getChildren();
		for (Object grandChildNode : grandChildNodes) {
			String grandChildNodeLabel = ((BaseNode) grandChildNode)
					.deriveNodeLabel();
			if ("configuration".equals(grandChildNodeLabel)) {
				configurationFile = ((BaseNode) grandChildNode)
						.getPhysicalStorage();
			}
		}		
		String riskFactorType = ConfigurationFileUtil.extractRootElementName(configurationFile);
		
		// If the riskfactor is continuous, the transition type is transitiondrift,
		// if the riskfactor is compound or categorical, the transition type is transitionmatrix,
		String transitionType;
		if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel().
				equalsIgnoreCase(riskFactorType)) {
			transitionType =  RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel();
		} else {
			transitionType = RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel();
		}
		// Create the action for the transition dialog (W21.1)
		InputBulletsFreeXMLFileAction action = 
			new InputBulletsFreeXMLFileAction(shell, treeViewer, 
					(DirectoryNode) selection.getFirstElement(),
					transitionType, Util.deriveEntityLabelAndValueFromRiskSourceNode((BaseNode)
							selection.getFirstElement())[0], riskFactorType);		
			
		//DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("New transitions file");
		//action.setSelectionPath(((BaseNode) selection.getFirstElement())
			//	.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * TODO Precondition: The selected Node maps to an XML file.
	 * 
	 * @param manager
	 * @param selection
	 * @throws DynamoConfigurationException 
	 */
	private void createMenu2EditXML(IMenuManager manager,
			IStructuredSelection selection) throws DynamoConfigurationException {
		FileNode node = (FileNode) selection.getFirstElement();
		String nodeLabel = node.toString();
		ParentNode parentNode = node.getParent();
		String parentNodeLabel = parentNode.toString();		
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		ParentNode greatGrandParentNode = ((ChildNode) grandParentNode).getParent();
		
		
		// TODO: REMOVE: just for debugging
		String rootElementNameDebug = ConfigurationFileUtil
		.extractRootElementName(node
				.getPhysicalStorage());
		log.debug("nodeLabel" + nodeLabel); //e.g. transitionmatrix, transitiondrift
		log.debug("rootElementNameDebug" + rootElementNameDebug); //e.g. transitionmatrix_zero, transitiondrift_zero		
		// TODO: REMOVE: just for debugging
				
				
		if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel()
				.equalsIgnoreCase(
						((BaseNode) grandParentNode).deriveNodeLabel())) {
			handlePopulations(manager, selection, node, nodeLabel);
		} else {
			if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
					.equalsIgnoreCase(
							((BaseNode) grandParentNode).deriveNodeLabel())) {
				/*
				XMLFileAction action = new XMLFileAction(shell, treeViewer,
						(BaseNode) node, /*"simulation" RootElementNamesEnum.SIMULATION.getNodeLabel(), nodeLabel);
				action.setText("Edit");
				manager.add(action);*/
				if ("configuration".equals(nodeLabel)) {
					String rootElementName = ConfigurationFileUtil
							.extractRootElementName(node
									.getPhysicalStorage());
					XMLFileAction action = new XMLFileAction(
							shell, treeViewer,
							(BaseNode) node, node
									.toString(),
							rootElementName);
					action.setText("Edit");
					manager.add(action);
				}
			} else {
				if (StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel()
						.equalsIgnoreCase(parentNodeLabel)) {
					XMLFileAction action = new XMLFileAction(shell, treeViewer,
							(BaseNode) node, node.toString(),
							"diseaseincidences");
					action.setText("Edit");
					manager.add(action);
				} else {
					if (StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel()
							.equalsIgnoreCase(parentNodeLabel)) {
						XMLFileAction action = new XMLFileAction(shell,
								treeViewer, (BaseNode) node, node.toString(),
								"diseaseprevalences");
						action.setText("Edit");
						manager.add(action);
					} else {
						if (StandardTreeNodeLabelsEnum.DALYWEIGHTS
								.getNodeLabel().equalsIgnoreCase(
										parentNodeLabel)) {
							XMLFileAction action = new XMLFileAction(shell,
									treeViewer, (BaseNode) node, node
											.toString(), RootElementNamesEnum.DALYWEIGHTS.getNodeLabel());
							action.setText("Edit");
							manager.add(action);
						} else {
							if (StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
									.getNodeLabel().equalsIgnoreCase(
											parentNodeLabel)) {
								XMLFileAction action = new XMLFileAction(shell,
										treeViewer, (BaseNode) node, node
												.toString(), RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel());
								action.setText("Edit");
								manager.add(action);
							} else {							
								if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
										.getNodeLabel().equalsIgnoreCase(
												parentNodeLabel)) {
									String actualRootElementName = ConfigurationFileUtil
											.extractRootElementName(node
													.getPhysicalStorage());
									XMLFileAction action = new XMLFileAction(shell,
											treeViewer, (BaseNode) node, node
													.toString(),
											actualRootElementName);
									action.setText("Edit");
									manager.add(action);
								} else {
									if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
											.getNodeLabel().equalsIgnoreCase(
													parentNodeLabel)) {
										String actualRootElementName = ConfigurationFileUtil
												.extractRootElementName(node
														.getPhysicalStorage());
										XMLFileAction action = new XMLFileAction(
												shell, treeViewer, (BaseNode) node,
												node.toString(),
												actualRootElementName);
										action.setText("Edit");
										manager.add(action);
									} else {
										// RISKFACTORS BLOCK
										if (StandardTreeNodeLabelsEnum.RISKFACTORS
												.getNodeLabel()
												.equalsIgnoreCase(
														((BaseNode) grandParentNode)
																.deriveNodeLabel())||
																StandardTreeNodeLabelsEnum.RISKFACTORS
																.getNodeLabel()
																.equalsIgnoreCase(
																		((BaseNode) greatGrandParentNode)
																				.deriveNodeLabel())															) {
											if ("configuration".equals(nodeLabel)) {
												String rootElementName = ConfigurationFileUtil
														.extractRootElementName(node
																.getPhysicalStorage());
												XMLFileAction action = new XMLFileAction(
														shell, treeViewer,
														(BaseNode) node, node
																.toString(),
														rootElementName);
												action.setText("Edit");
												manager.add(action);
											} else {
												if ("prevalence".equals(nodeLabel)) {
													String rootElementName = ConfigurationFileUtil
															.extractRootElementName(node
																	.getPhysicalStorage());
													XMLFileAction action = new XMLFileAction(
															shell, treeViewer,
															(BaseNode) node, node
																	.toString(),
															rootElementName);
													action.setText("Edit");
													manager.add(action);
												} else {
													if ("durationdistribution"
															.equals(nodeLabel)) {
														addDummy(manager,
																selection,
																"riskfactors-durationdistribution.xml");
													} else {
														if ("relriskfordeath"
																.equals(nodeLabel)) {
															String rootElementName = ConfigurationFileUtil
																	.extractRootElementName(node
																			.getPhysicalStorage());
															XMLFileAction action = new XMLFileAction(
																	shell,
																	treeViewer,
																	(BaseNode) node,
																	node.toString(),
																	rootElementName);
															action.setText("Edit");
															manager.add(action);
														} else {
															if ("relriskfordisability"
																	.equals(nodeLabel)) {
																String rootElementName = ConfigurationFileUtil
																		.extractRootElementName(node
																				.getPhysicalStorage());
																XMLFileAction action = new XMLFileAction(
																		shell,
																		treeViewer,
																		(BaseNode) node,
																		node
																				.toString(),
																		rootElementName);
																action
																		.setText("Edit");
																manager.add(action);
	
															// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
															// TODO: EDIT THE EXISTING FILE: Retrieve the  StandardTreeNodeLabelsEnum
															// Here, the transition files (i.e. transitiondrift, transitiondrift_zero,
															// transitiondrift_netto, transitionmatrix_zero, transitionmatrix_netto)
															// have to be edited, "transitions" is a temporary dummy notation!
															// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO														
															} else {											
																if ("transitiondrift".equals(ConfigurationFileUtil
																				.extractRootElementName(node
																						.getPhysicalStorage()))) {																
																	String rootElementName = ConfigurationFileUtil
																			.extractRootElementName(node
																					.getPhysicalStorage());
																	// TODO: Create screen W21 Transition Drift
																	XMLFileAction action = new XMLFileAction(
																			this.shell, this.treeViewer,
																			(BaseNode) node, node
																					.toString(),
																			rootElementName);
																	action.setText("Edit");
																	manager.add(action);
																} else {
																	if ("transitiondrift_netto".equals(ConfigurationFileUtil
																			.extractRootElementName(node
																					.getPhysicalStorage()))) {																
																		String rootElementName = ConfigurationFileUtil
																				.extractRootElementName(node
																						.getPhysicalStorage());
																		// TODO: Create screen W21 Transition Drift Netto
																		XMLFileAction action = new XMLFileAction(
																				this.shell, this.treeViewer,
																				(BaseNode) node, node
																						.toString(),
																				rootElementName);
																		action.setText("Edit");
																		manager.add(action);
																	} else {																																
																		if ("transitiondrift_zero".equals(ConfigurationFileUtil
																				.extractRootElementName(node
																						.getPhysicalStorage()))) {																		
																			// File cannot be edited, only created, no further actions
																			addTransactionNoAction(manager,
																					selection,
																					"Selected file cannot be edited");																																	
																		} else {											
																			if ("transitionmatrix".equals(ConfigurationFileUtil
																							.extractRootElementName(node
																									.getPhysicalStorage()))) {																		
																				String rootElementName = ConfigurationFileUtil
																						.extractRootElementName(node
																								.getPhysicalStorage());
																				XMLFileAction action = new XMLFileAction(
																						this.shell, this.treeViewer,
																						(BaseNode) node, node
																								.toString(),
																						rootElementName);
																				action.setText("Edit");
																				manager.add(action);
																			} else {											
																				if ("transitionmatrix_netto".equals(ConfigurationFileUtil
																						.extractRootElementName(node
																								.getPhysicalStorage())) || 
																						"transitionmatrix_zero".equals(ConfigurationFileUtil
																						.extractRootElementName(node
																								.getPhysicalStorage()))) {
																					// File cannot be edited, only created, no further actions
																					addTransactionNoAction(manager,
																							selection,
																							"Selected file cannot be edited");																			
																				} else {
																					addDummy(manager,
																							selection,
																							"Not implemented (yet)");
																				}
																			}
																		}
																	}
																}
															}
													 	}
													}
												}
											}
										}
									}
									// RISKFACTORS BLOCK
								}
							}
						}
					}
				}
			}
		}
	}

	private void addDummy(IMenuManager manager, IStructuredSelection selection,
			String facText) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Dummy: \"" + facText + "\"");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}
	
	
	public void addTransactionNoAction(IMenuManager manager, IStructuredSelection selection,
			String facText) {
		TransitionNoAction action = new TransitionNoAction(shell);
		action.setText(facText);
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
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
					addDummy(manager, selection, "");
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