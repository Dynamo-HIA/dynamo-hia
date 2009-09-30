package nl.rivm.emi.dynamo.ui.treecontrol.menu;

/**
 * TODO Get rootelement-names from the FileControlEnum.
 * 
 */
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.actions.DurationDistributionFreeXMLFilePlusTypeBulletsAction;
import nl.rivm.emi.dynamo.ui.actions.DynamoHIADummyDebugAction;
import nl.rivm.emi.dynamo.ui.actions.FreeName4RiskFactorXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.FreeNameXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.InputBulletsFreeXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.OverallDALYWeightsXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.OverallMortalityXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.PopulationSizeXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.RelativeRiskFromRiskSourceAction;
import nl.rivm.emi.dynamo.ui.actions.SimulationUniversalAction;
import nl.rivm.emi.dynamo.ui.actions.TransitionNoAction;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.create.NewDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.create.NewbornsXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.delete.DeleteDirectoryAction;
import nl.rivm.emi.dynamo.ui.actions.delete.DeleteXMLFileAction;
import nl.rivm.emi.dynamo.ui.actions.delete.MessageStrings;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.ui.util.RiskFactorStringConstantsEnum;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuFactory {

	Log log = LogFactory.getLog(this.getClass().getName());

	Shell shell;
	TreeViewer treeViewer;
	RiskFactorContextMenuFactory contextMenuFactory = new RiskFactorContextMenuFactory();

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
			createErrorMenu4UnexpectedNodes(manager, selection);
			break;
		case 2:
			// Simulations, ReferenceData
			handleLevel2(manager, selection, treeDepth, nodeLabel);
			break;
		case 3:
			// <Simulation-name>, Populations, RiskFactors, Diseases.
			handleLevel3(manager, selection, selectedNode, treeDepth, nodeLabel);
			break;
		case 4:
			// Modelconfiguration, Parameters, Results, <Scenario-name>,
			// <Population-name>, <RiskFactor-name>,
			// <Disease-name>.
			handleLevel4(manager, selection, selectedNode, treeDepth, nodeLabel);
			break;
		case 5:
			handleLevel5(manager, selection, selectedNode, nodeLabel);
			break;
		case 6:
			handleLevel6(manager, selection, selectedNode);
			break;
		default:
			createErrorMenu4UnexpectedNodes(manager, selection);
			break;
		}
	}

	private void handleLevel2(IMenuManager manager,
			IStructuredSelection selection, int treeDepth, String nodeLabel) {
		if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
				.equalsIgnoreCase(nodeLabel)) {
			createMenu4Simulations(manager, selection);
		} else {
			if (StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createErrorMenu4UnexpectedNodes(manager, selection);
			} else {
				createErrorMenu4UnexpectedNodes(manager, selection);
			}
		}
	}

	private void handleLevel3(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode,
			int treeDepth, String nodeLabel) {
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
					if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
							.equalsIgnoreCase(parentLabel)) {
						createMenu4Simulation(manager, selection);

					} else {
						createErrorMenu4UnexpectedNodes(manager, selection);
					}
				}
			}
		}
	}

	private void handleLevel4(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode,
			int treeDepth, String nodeLabel) throws TreeStructureException,
			DynamoConfigurationException {
		if (StandardTreeNodeLabelsEnum.MODELCONFIGURATION.getNodeLabel()
				.equalsIgnoreCase(nodeLabel)) {
			createInformationMenu4UnimplementedNodes(manager, selection,
					treeDepth);
		} else {
			if (StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createInformationMenu4UnimplementedNodes(manager, selection,
						treeDepth);
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
					if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel()
							.equalsIgnoreCase(parentLabel)) {
						createMenu4Population(manager, selection);
					} else {
						if (StandardTreeNodeLabelsEnum.RISKFACTORS
								.getNodeLabel().equalsIgnoreCase(parentLabel)) {
							createMenu4RiskFactor(manager, selection);
						} else {
							if (StandardTreeNodeLabelsEnum.DISEASES
									.getNodeLabel().equalsIgnoreCase(
											parentLabel)) {
								createMenu4Disease(manager, selection);
							} else {
								ParentNode grandParentNode = ((ChildNode) parentNode)
										.getParent();
								String grandParentLabel = ((BaseNode) grandParentNode)
										.deriveNodeLabel();
								if (StandardTreeNodeLabelsEnum.SIMULATIONS
										.getNodeLabel().equalsIgnoreCase(
												grandParentLabel)) {
									createMenu2EditXML(manager, selection);
									// createMenu4Scenario(manager,
									// selection);
								} else {
									createErrorMenu4UnexpectedNodes(manager,
											selection);
								}
							}
						}
					}
				}
			}
		}
	}

	private void handleLevel5(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode,
			String nodeLabel) throws DynamoConfigurationException,
			ConfigurationException {
		if (selectedNode.isXMLFile()) {
			createMenu2EditXML(manager, selection);
		} else {
			ParentNode parentNode = ((ChildNode) selectedNode).getParent();
			ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
			String grandParentLabel = ((BaseNode) grandParentNode)
					.deriveNodeLabel();
			if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
					.equalsIgnoreCase(grandParentLabel)) {
				buildRiskFactorsMenus(manager, selection, nodeLabel);
			} else {
				if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
						.equalsIgnoreCase(grandParentLabel)) {
					buildDiseasesMenus(manager, selection, nodeLabel);
				} else {
					createErrorMenu4UnexpectedNodes(manager, selection);
				}
			}
		}
	}

	private void handleLevel6(IMenuManager manager,
			IStructuredSelection selection, BaseNode selectedNode)
			throws DynamoConfigurationException {
		if (selectedNode.isXMLFile()) {
			createMenu2EditXML4Level6(manager, selection);
		} else {
			createErrorMenu4UnexpectedNodes(manager, selection);
		}
	}

	private void buildDiseasesMenus(IMenuManager manager,
			IStructuredSelection selection, String nodeLabel) {
		if (StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel()
				.equalsIgnoreCase(nodeLabel)) {
			createMenu4DALY_Weights(manager, selection);
		} else {
			if (StandardTreeNodeLabelsEnum.EXCESSMORTALITIES.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createMenu4Excess_Mortalities(manager, selection);
			} else {
				if (StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel()
						.equalsIgnoreCase(nodeLabel)) {
					createMenu4Incidences(manager, selection);
				} else {
					if (StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel()
							.equalsIgnoreCase(nodeLabel)) {
						createMenu4Prevalence(manager, selection);
					} else {
						if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
								.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
							createMenu4RelRiskFromDiseases(manager, selection);
						} else {
							if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
									.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
								createMenu4RelRiskFromRiskFactor(manager,
										selection);
							} else {
								createErrorMenu4UnexpectedNodes(manager,
										selection);
							}
						}
					}
				}
			}
		}
	}

	private void buildRiskFactorsMenus(IMenuManager manager,
			IStructuredSelection selection, String nodeLabel)
			throws ConfigurationException {
		BaseNode riskFactorNameDirectoryNode = (BaseNode) ((ChildNode) selection
				.getFirstElement()).getParent();
		if (ConfigurationFileUtil
				.exceptionFreeExtractRootElementNameFromChildConfiguration(riskFactorNameDirectoryNode) != null) {
			if (StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel()
					.equalsIgnoreCase(nodeLabel)) {
				createMenu4RiskFactorPrevalence(manager, selection);
			} else {
				if (StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
						.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
					createMenu4RiskFactorDurationDistribution(manager,
							selection);
				} else {
					if (StandardTreeNodeLabelsEnum.TRANSITIONSDIR
							.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
						createMenu4RiskFactorTransitions(manager, selection);
					} else {
						if (StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR
								.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
							createMenu4RiskFactorRelRisksForDeath(manager,
									selection);
						} else {
							if (StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
									.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
								createMenu4RiskFactorRelRisksForDisability(
										manager, selection);
							} else {
								createErrorMenu4UnexpectedNodes(manager,
										selection);
							}
						}
					}
				}
			}
		} else {
			createMenu2Ask4RiskFactorConfiguration(manager, selection);
		}
	}

	/**
	 * Error menu to indicate the choice of this Node was unexpected.
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu2Ask4RiskFactorConfiguration(
			final IMenuManager manager, IStructuredSelection selection) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action
				.setText("Please create the configuration for this riskfactor first(right click on riskfactor name).");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	/**
	 * Use-case 1 of the SimulationUniversalAction.
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Simulations(final IMenuManager manager,
			IStructuredSelection selection) {
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		SimulationUniversalAction sNAction = new SimulationUniversalAction(
				shell, treeViewer, selectedNode, "simulation");
		sNAction.setText(sNAction.getMenuLabel());
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
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		SimulationUniversalAction sNAction = new SimulationUniversalAction(
				shell, treeViewer, selectedNode, "simulation");
		sNAction.setText(sNAction.getMenuLabel());
		manager.add(sNAction);
		// Deleting...
		MessageStrings theMessageStrings = new MessageStrings(
				"Removing Simulation.",
				"The simulation directory ",
				" and\nALL of its contents will be deleted.\nIs that what you want?",
				"The Simulation still has configuration files.\n"
						+ "You must delete them all first.");
		DeleteDirectoryAction deleteDiseaseAction = new DeleteDirectoryAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				theMessageStrings, 10);
		deleteDiseaseAction.setText("Delete simulation");
		manager.add(deleteDiseaseAction);
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
		Set<String> allPossibleChildren = new HashSet<String>();
		allPossibleChildren.add(StandardTreeNodeLabelsEnum.POPULATIONSIZEFILE
				.getNodeLabel());
		allPossibleChildren
				.add(StandardTreeNodeLabelsEnum.POPULATIONOVERALLMORTALITYFILE
						.getNodeLabel());
		allPossibleChildren
				.add(StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE
						.getNodeLabel());
		allPossibleChildren
				.add(StandardTreeNodeLabelsEnum.POPULATIONOVERALLDALYWEIGHTSFILE
						.getNodeLabel());
		DirectoryNode parentNode = (DirectoryNode) selection.getFirstElement();
		Object[] childNodes = parentNode.getChildren();
		for (Object childNode : childNodes) {
			if (childNode instanceof FileNode) {
				String fileNodeLabel = ((BaseNode) childNode).deriveNodeLabel();
				allPossibleChildren.remove(fileNodeLabel);
			}
		}
		// Calling screen W11
		XMLFileAction action = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				StandardTreeNodeLabelsEnum.POPULATIONSIZEFILE.getNodeLabel(),
				"populationsize");
		action.setText("New populationsize");
		action.setEnabled(allPossibleChildren
				.contains(StandardTreeNodeLabelsEnum.POPULATIONSIZEFILE
						.getNodeLabel()));
		manager.add(action);

		// Calling screen W12
		XMLFileAction action2 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overallmortality",
				StandardTreeNodeLabelsEnum.POPULATIONOVERALLMORTALITYFILE
						.getNodeLabel());
		action2.setText("New overall mortality");
		action2
				.setEnabled(allPossibleChildren
						.contains(StandardTreeNodeLabelsEnum.POPULATIONOVERALLMORTALITYFILE
								.getNodeLabel()));
		manager.add(action2);

		// Calling screen W13
		XMLFileAction action3 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE
						.getNodeLabel(),
				StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE
						.getNodeLabel());
		action3.setText("New newborns");
		action3.setEnabled(allPossibleChildren
				.contains(StandardTreeNodeLabelsEnum.POPULATIONNEWBORNSFILE
						.getNodeLabel()));
		manager.add(action3);

		// Calling screen W14
		XMLFileAction action4 = new XMLFileAction(this.shell, this.treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				"overalldalyweights",
				StandardTreeNodeLabelsEnum.POPULATIONOVERALLDALYWEIGHTSFILE
						.getNodeLabel());
		action4.setText("New overall DALY weights");
		action4
				.setEnabled(allPossibleChildren
						.contains(StandardTreeNodeLabelsEnum.POPULATIONOVERALLDALYWEIGHTSFILE
								.getNodeLabel()));
		manager.add(action4);
		// Deleting the population-directory..
		MessageStrings theMessageStrings = new MessageStrings(
				"Removing population.",
				"The population directory ",
				" and\nALL of its contents will be deleted.\nIs that what you want?",
				"The population still has configuration files.\n"
						+ "You must delete them all first");
		DeleteDirectoryAction action5 = new DeleteDirectoryAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				theMessageStrings, 1);
		action5.setText("Delete population");
		manager.add(action5);
	}

	// ** Risk Factors **

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
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactorPrevalence(IMenuManager manager,
			IStructuredSelection selection) {
		FreeName4RiskFactorXMLFileAction action = new FreeName4RiskFactorXMLFileAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				RiskFactorStringConstantsEnum.RISKFACTORPREVALENCES);
		action.setText("New risk factor prevalences file");
		manager.add(action);
	}

	/**
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactorDurationDistribution(
			IMenuManager manager, IStructuredSelection selection) {
		DurationDistributionFreeXMLFilePlusTypeBulletsAction action = new DurationDistributionFreeXMLFilePlusTypeBulletsAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
				"aaa_riskfactorname", "bbb_riskfactortype");
		action.setText("New risk factor duration distribution file");
		manager.add(action);
	}

	/**
	 * TODO implement create new transitions file xxxxxxxxxxxxxxxxxxxxxxsss
	 * 
	 * @param manager
	 * @param selection
	 * @throws ConfigurationException
	 */
	private void createMenu4RiskFactorTransitions(IMenuManager manager,
			IStructuredSelection selection) throws ConfigurationException {
		String riskFactorType = getRiskFactorConfigurationRootElementName(selection);

		// If the riskfactor is continuous, the transition type is
		// transitiondrift,
		// if the riskfactor is compound or categorical, the transition type
		// is
		// transitionmatrix,
		String transitionType;
		if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel()
				.equalsIgnoreCase(riskFactorType)) {
			transitionType = RootElementNamesEnum.TRANSITIONDRIFT
					.getNodeLabel();
		} else {
			transitionType = RootElementNamesEnum.TRANSITIONMATRIX
					.getNodeLabel();
		}
		// Create the action for the transition dialog (W21.1)
		InputBulletsFreeXMLFileAction action = new InputBulletsFreeXMLFileAction(
				shell,
				treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				transitionType,
				Util
						.deriveEntityLabelAndValueFromRiskSourceNode((BaseNode) selection
								.getFirstElement())[0], riskFactorType);

		action.setText("New transitions file");
		manager.add(action);
	}

	private String getRiskFactorConfigurationRootElementName(
			IStructuredSelection selection) throws DynamoConfigurationException {
		File configurationFile = null;
		DirectoryNode transitionsDirectoryNode = (DirectoryNode) selection
				.getFirstElement();

		Object[] siblingNodes = ((ParentNode) transitionsDirectoryNode
				.getParent()).getChildren();
		for (Object siblingNode : siblingNodes) {
			String siblingNodeLabel = ((BaseNode) siblingNode)
					.deriveNodeLabel();
			if ("configuration".equals(siblingNodeLabel)) {
				configurationFile = ((BaseNode) siblingNode)
						.getPhysicalStorage();
			}
		}
		String riskFactorType = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(configurationFile);
		return riskFactorType;
	}

	/**
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactorRelRisksForDeath(IMenuManager manager,
			IStructuredSelection selection) {
		FreeName4RiskFactorXMLFileAction action = new FreeName4RiskFactorXMLFileAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDEATH);
		action.setText("New risk factor relative risks for death file");
		manager.add(action);
	}

	/**
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RiskFactorRelRisksForDisability(
			IMenuManager manager, IStructuredSelection selection) {
		FreeName4RiskFactorXMLFileAction action = new FreeName4RiskFactorXMLFileAction(
				shell,
				treeViewer,
				(DirectoryNode) selection.getFirstElement(),
				RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDISABILITY);
		action.setText("New risk factor relative risks for disability file");
		manager.add(action);
	}

	// ** DISEASES **
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
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Disease(IMenuManager manager,
			IStructuredSelection selection) {
		MessageStrings theMessageStrings = new MessageStrings(
				"Removing Disease.",
				"The disease directory ",
				" and\nALL of its contents will be deleted.\nIs that what you want?",
				"The Disease still has configuration files.\n"
						+ "You must delete them all first.");
		DeleteDirectoryAction deleteDiseaseAction = new DeleteDirectoryAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				theMessageStrings, 2);
		deleteDiseaseAction.setText("Delete disease");
		manager.add(deleteDiseaseAction);
	}

	/**
	 * @param manager
	 * @param selection
	 */
	private void createMenu4Prevalence(IMenuManager manager,
			IStructuredSelection selection) {
		FreeNameXMLFileAction action = new FreeNameXMLFileAction(shell,
				treeViewer, (DirectoryNode) selection.getFirstElement(),
				"diseaseprevalences", new FileAndDirectoryNameInputValidator());
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
				"dalyweights", new FileAndDirectoryNameInputValidator());
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
				RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel(),
				new FileAndDirectoryNameInputValidator());
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
				"diseaseincidences", new FileAndDirectoryNameInputValidator());
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
		IInputValidator theValidator = new FileAndDirectoryNameInputValidator();
		RelativeRiskFromRiskSourceAction action = new RelativeRiskFromRiskSourceAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				RootElementNamesEnum.RELATIVERISKSFROMDISEASE.getNodeLabel(),
				theValidator);
		action.setText("New relative risks from other disease file");
		manager.add(action);
		// Testing
		// IInputValidator theValidator = new
		// FileAndDirectoryNameInputValidator();
		// FreeNamePlusDropDownXMLFileAction action2 = new
		// FreeNamePlusDropDownXMLFileAction(
		// shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
		// RootElementNamesEnum.RELATIVERISKSFROMDISEASE.getNodeLabel(),
		// theValidator);
		// action2.setText("Testing new box");
		// manager.add(action2);
	}

	/**
	 * TODO
	 * 
	 * @param manager
	 * @param selection
	 */
	private void createMenu4RelRiskFromRiskFactor(IMenuManager manager,
			IStructuredSelection selection) {
		IInputValidator theValidator = new FileAndDirectoryNameInputValidator();
		RelativeRiskFromRiskSourceAction action = new RelativeRiskFromRiskSourceAction(
				shell, treeViewer, (DirectoryNode) selection.getFirstElement(),
				null, theValidator);
		action.setText("New relative risks from risk factor file");
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
		// String parentNodeLabel = parentNode.toString();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		// ParentNode greatGrandParentNode = ((ChildNode) grandParentNode)
		// .getParent();
		if (StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel()
				.equalsIgnoreCase(
						((BaseNode) grandParentNode).deriveNodeLabel())) {
			createEditMenu4XML4Populations(manager, selection, node, nodeLabel);
		} else {
			if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
					.equalsIgnoreCase(
							((BaseNode) grandParentNode).deriveNodeLabel())) {
				createEditMenu4XML4Simulations(manager, node, nodeLabel);
			} else {
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
						.equalsIgnoreCase(
								((BaseNode) grandParentNode).deriveNodeLabel())) {
					if ("configuration".equals(nodeLabel)) {
						handleXMLs(manager, node);
					} else {
					}
				}
			}
		}
	}

	/**
	 * TODO Precondition: The selected Node maps to an XML file.
	 * 
	 * @param manager
	 * @param selection
	 * @throws DynamoConfigurationException
	 */
	private void createMenu2EditXML4Level6(IMenuManager manager,
			IStructuredSelection selection) throws DynamoConfigurationException {
		FileNode node = (FileNode) selection.getFirstElement();
		String nodeLabel = node.toString();
		ParentNode parentNode = node.getParent();
		String parentNodeLabel = parentNode.toString();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		ParentNode greatGrandParentNode = ((ChildNode) grandParentNode)
				.getParent();

		// TODO: REMOVE: just for debugging
		String rootElementNameDebug = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(node
						.getPhysicalStorage());
		log.debug("nodeLabel" + nodeLabel); // e.g. transitionmatrix,
		// transitiondrift
		log.debug("rootElementNameDebug" + rootElementNameDebug); // e.g.
		if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
				.equalsIgnoreCase(
						((BaseNode) greatGrandParentNode).deriveNodeLabel())) {
			createEditMenu4XML4Diseases(manager, node, parentNodeLabel);
		} else {
			if (StandardTreeNodeLabelsEnum.RISKFACTORS
					.getNodeLabel()
					.equalsIgnoreCase(
							((BaseNode) greatGrandParentNode).deriveNodeLabel())) {
				createEditMenu4NonConfigXML4RiskFactors(manager, selection,
						node, nodeLabel);
			}
		}
	}

	private void createEditMenu4NonConfigXML4RiskFactors(IMenuManager manager,
			IStructuredSelection selection, FileNode node, String nodeLabel)
			throws DynamoConfigurationException {
		ParentNode parentNode = node.getParent();
		String parentNodeLabel = ((BaseNode) parentNode).deriveNodeLabel();
		if (StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel()
				.equalsIgnoreCase(parentNodeLabel)) {
			handleRiskFactorPrevalencesXMLs(manager, selection, node);
		} else {
			if (StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel()
					.equalsIgnoreCase(parentNodeLabel)) {
				handleXMLs(manager, node);
			} else {
				if (StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
						.getNodeLabel().equalsIgnoreCase(parentNodeLabel)) {
					handleXMLs(manager, node);
				} else {
					if (StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel()
							.equalsIgnoreCase(parentNodeLabel)) {
						handleRiskFactorTransitionsXMLs(manager, selection,
								node);
					} else {
						if (StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
								.getNodeLabel().equalsIgnoreCase(
										parentNodeLabel)) {
							handleRiskFactorDurationDistributionXMLs(manager,
									selection, node);
						}
					}
				}
			}
		}
	}

	private void handleXMLs(IMenuManager manager, FileNode node)
			throws DynamoConfigurationException {
		String rootElementName = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(node
						.getPhysicalStorage());
		XMLFileAction action = new XMLFileAction(shell, treeViewer,
				(BaseNode) node, node.toString(), rootElementName);
		action.setText("Edit");
		manager.add(action);

		if (!RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel().equals(
				rootElementName)
				&& !RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel()
						.equals(rootElementName)
				&& !RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
						.equals(rootElementName)) {
			addFileDeleteAction(manager, node);
		} else {
			ParentNode parentNode = ((ChildNode) node).getParent();
			MessageStrings theMessageStrings = new MessageStrings(
					"Removing risk-factor.",
					"The risk-factor directory ",
					" and\nALL of its contents will be deleted.\nIs that what you want?",
					"The Risk-Factor still has configuration/data files.\n"
							+ "You must delete them all first");
			DeleteDirectoryAction delDirAction = new DeleteDirectoryAction(
					shell, treeViewer, (DirectoryNode)parentNode,
					theMessageStrings, 2);
			delDirAction.setText("Delete riskfactor");
			manager.add(delDirAction);
		}
	}

	private void handleRiskFactorPrevalencesXMLs(IMenuManager manager,
			IStructuredSelection selection, FileNode node)
			throws DynamoConfigurationException {
		if (RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
				.getNodeLabel()
				.equals(
						ConfigurationFileUtil
								.extractRootElementNameIncludingSchemaCheck(node
										.getPhysicalStorage()))) {
			handleXMLs(manager, node);
		} else {
			if (RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
					.getNodeLabel()
					.equals(
							ConfigurationFileUtil
									.extractRootElementNameIncludingSchemaCheck(node
											.getPhysicalStorage()))) {
				handleXMLs(manager, node);
			} else {
				addDummy(manager, selection, "Unexpected rootelementname.");
			}
		}
	}

	private void handleRiskFactorTransitionsXMLs(IMenuManager manager,
			IStructuredSelection selection, FileNode node)
			throws DynamoConfigurationException {
		if ("transitiondrift".equals(ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(node
						.getPhysicalStorage()))) {
			String rootElementName = ConfigurationFileUtil
					.extractRootElementNameIncludingSchemaCheck(node
							.getPhysicalStorage());
			// TODO: Create screen W21 Transition Drift
			XMLFileAction action = new XMLFileAction(this.shell,
					this.treeViewer, (BaseNode) node, node.toString(),
					rootElementName);
			action.setText("Edit");
			manager.add(action);
			addFileDeleteAction(manager, node);
		} else {
			if ("transitiondrift_netto".equals(ConfigurationFileUtil
					.extractRootElementNameIncludingSchemaCheck(node
							.getPhysicalStorage()))) {
				String rootElementName = ConfigurationFileUtil
						.extractRootElementNameIncludingSchemaCheck(node
								.getPhysicalStorage());
				// TODO: Create screen W21 Transition
				// Drift Netto
				XMLFileAction action = new XMLFileAction(this.shell,
						this.treeViewer, (BaseNode) node, node.toString(),
						rootElementName);
				action.setText("Edit");
				manager.add(action);
				addFileDeleteAction(manager, node);
			} else {
				if ("transitiondrift_zero".equals(ConfigurationFileUtil
						.extractRootElementNameIncludingSchemaCheck(node
								.getPhysicalStorage()))) {
					// File
					// cannot
					// be
					// edited,
					// only
					// created,
					// no
					// further
					// actions
					addTransactionNoAction(manager, selection,
							"Selected file cannot be edited");
					addFileDeleteAction(manager, node);
				} else {
					if ("transitionmatrix".equals(ConfigurationFileUtil
							.extractRootElementNameIncludingSchemaCheck(node
									.getPhysicalStorage()))) {
						String rootElementName = ConfigurationFileUtil
								.extractRootElementNameIncludingSchemaCheck(node
										.getPhysicalStorage());
						XMLFileAction action = new XMLFileAction(this.shell,
								this.treeViewer, (BaseNode) node, node
										.toString(), rootElementName);
						action.setText("Edit");
						manager.add(action);
						addFileDeleteAction(manager, node);
					} else {
						if ("transitionmatrix_netto"
								.equals(ConfigurationFileUtil
										.extractRootElementNameIncludingSchemaCheck(node
												.getPhysicalStorage()))
								|| "transitionmatrix_zero"
										.equals(ConfigurationFileUtil
												.extractRootElementNameIncludingSchemaCheck(node
														.getPhysicalStorage()))) {
							// File cannot be edited, only
							// created, no
							// further actions
							addTransactionNoAction(manager, selection,
									"Selected file cannot be edited");
							addFileDeleteAction(manager, node);
						} else {
							addDummy(manager, selection,
									"Not implemented (yet)");
						}
					}
				}
			}
		}
	}

	private void handleRiskFactorDurationDistributionXMLs(IMenuManager manager,
			IStructuredSelection selection, FileNode node)
			throws DynamoConfigurationException {
		String rootElementName = ConfigurationFileUtil
				.extractRootElementNameIncludingSchemaCheck(node
						.getPhysicalStorage());
		XMLFileAction action = new XMLFileAction(this.shell, this.treeViewer,
				(BaseNode) node, node.toString(), rootElementName);
		action.setText("Edit");
		manager.add(action);
		addFileDeleteAction(manager, node);
	}

	private void createEditMenu4XML4Diseases(IMenuManager manager,
			FileNode node, String parentNodeLabel)
			throws DynamoConfigurationException {
		if (StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel()
				.equalsIgnoreCase(parentNodeLabel)) {
			XMLFileAction action = new XMLFileAction(shell, treeViewer,
					(BaseNode) node, node.toString(), "diseaseincidences");
			action.setText("Edit");
			manager.add(action);
			addFileDeleteAction(manager, node);
		} else {
			if (
			/*
			 * BUG! RootElementNamesEnum.DISEASEPREVALENCES
			 * .equals(ConfigurationFileUtil.extractRootElementName(node
			 * .getPhysicalStorage()))
			 */
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel()
					.equalsIgnoreCase(parentNodeLabel)) {
				String rootElementName = "diseaseprevalences";
				XMLFileAction action = new XMLFileAction(shell, treeViewer,
						(BaseNode) node, node.toString(), rootElementName);
				action.setText("Edit");
				manager.add(action);
				addFileDeleteAction(manager, node);
			} else {
				if (StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel()
						.equalsIgnoreCase(parentNodeLabel)) {
					XMLFileAction action = new XMLFileAction(shell, treeViewer,
							(BaseNode) node, node.toString(),
							RootElementNamesEnum.DALYWEIGHTS.getNodeLabel());
					action.setText("Edit");
					manager.add(action);
					addFileDeleteAction(manager, node);
				} else {
					if (StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
							.getNodeLabel().equalsIgnoreCase(parentNodeLabel)) {
						XMLFileAction action = new XMLFileAction(shell,
								treeViewer, (BaseNode) node, node.toString(),
								RootElementNamesEnum.EXCESSMORTALITY
										.getNodeLabel());
						action.setText("Edit");
						manager.add(action);
						addFileDeleteAction(manager, node);
					} else {
						if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
								.getNodeLabel().equalsIgnoreCase(
										parentNodeLabel)) {
							handleXMLs(manager, node);
						} else {
							if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
									.getNodeLabel().equalsIgnoreCase(
											parentNodeLabel)) {
								handleXMLs(manager, node);
							}
						}
					}
				}
			}
		}
	}

	private void addFileDeleteAction(IMenuManager manager, FileNode node) {
		DeleteXMLFileAction deleteAction = new DeleteXMLFileAction(shell,
				treeViewer, (BaseNode) node, node.toString());
		deleteAction.setText("Delete");
		manager.add(deleteAction);
	}

	private void createEditMenu4XML4Simulations(IMenuManager manager,
			FileNode node, String nodeLabel)
			throws DynamoConfigurationException {
		if ("configuration".equals(nodeLabel)) {
			String rootElementName = ConfigurationFileUtil
					.justExtractRootElementName(node.getPhysicalStorage());
			// XMLFileAction action = new XMLFileAction(shell, treeViewer,
			// (BaseNode) node, node.toString(), rootElementName);
			// action.setConfigurationFileExists(true);
			// action.setText("Edit");
			SimulationUniversalAction action = new SimulationUniversalAction(
					shell, treeViewer, node, nodeLabel);
			action.setText(action.getMenuLabel());
			manager.add(action);
			addFileDeleteAction(manager, node);
		}
	}

	private void addDummy(IMenuManager manager, IStructuredSelection selection,
			String facText) {
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("Unsupported: \"" + facText + "\"");
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	public void addTransactionNoAction(IMenuManager manager,
			IStructuredSelection selection, String facText) {
		TransitionNoAction action = new TransitionNoAction(shell);
		action.setText(facText);
		action.setSelectionPath(((BaseNode) selection.getFirstElement())
				.getPhysicalStorage().getAbsolutePath());
		manager.add(action);
	}

	private void createEditMenu4XML4Populations(IMenuManager manager,
			IStructuredSelection selection, FileNode node, String nodeLabel) {
		if ("size".equals(nodeLabel)) {
			PopulationSizeXMLFileAction action = new PopulationSizeXMLFileAction(
					shell, treeViewer, (BaseNode) node, "populationsize");
			action.setText("Edit");
			manager.add(action);
			addFileDeleteAction(manager, node);
		} else {
			if ("overallmortality".equals(nodeLabel)) {
				OverallMortalityXMLFileAction action = new OverallMortalityXMLFileAction(
						shell, treeViewer, (BaseNode) node, "overallmortality");
				action.setText("Edit");
				manager.add(action);
				addFileDeleteAction(manager, node);
			} else {
				if ("overalldalyweights".equals(nodeLabel)) {
					OverallDALYWeightsXMLFileAction action = new OverallDALYWeightsXMLFileAction(
							shell, treeViewer, (BaseNode) node,
							"overalldalyweights");
					action.setText("Edit");
					manager.add(action);
					addFileDeleteAction(manager, node);
				} else {
					if ("newborns".equals(nodeLabel)) {
						NewbornsXMLFileAction action = new NewbornsXMLFileAction(
								shell, treeViewer, (BaseNode) node, "newborns");
						action.setText("Edit");
						manager.add(action);
						addFileDeleteAction(manager, node);
					} else {
						addDummy(manager, selection, "");
					}
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
	 */
	private void createErrorMenu4UnexpectedNodes(final IMenuManager manager,
			IStructuredSelection selection) {
		BaseNode selectedNode = (BaseNode) selection.getFirstElement();
		// String selectionPath = selectedNode.getPhysicalStorage()
		// .getAbsolutePath();
		DynamoHIADummyDebugAction action = new DynamoHIADummyDebugAction(shell);
		action.setText("No action possible");
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