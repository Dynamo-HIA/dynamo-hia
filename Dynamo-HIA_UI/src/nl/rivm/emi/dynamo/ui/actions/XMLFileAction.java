package nl.rivm.emi.dynamo.ui.actions;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.DiseaseIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.DiseasePrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.ExcessMortalityModal;
import nl.rivm.emi.dynamo.ui.main.NewbornsModal;
import nl.rivm.emi.dynamo.ui.main.OverallDALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.OverallMortalityModal;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromOtherDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class XMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String fileNameTrunk;
	private String rootElementName;
	private boolean configurationFileExists;

	public XMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String fileNameTrunk, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.fileNameTrunk = fileNameTrunk;
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		String filePath = "";
		if (node instanceof DirectoryNode) {
			filePath = node.getPhysicalStorage().getAbsolutePath()
					+ File.separator + fileNameTrunk + ".xml";
		} else {
			filePath = node.getPhysicalStorage().getAbsolutePath();
		}
		File savedFile = new File(filePath);

		// Process the data and set the location of the file to be saved
		processThroughModal(savedFile, savedFile);
	}

	public void processThroughModal(File dataFile, File savedFile) {
		try {
			boolean isOld = savedFile.exists();
			Runnable theModal = null;

			if (RootElementNamesEnum.POPULATIONSIZE.getNodeLabel().equals(
					rootElementName)) {
				theModal = new PopulationSizeModal(shell, dataFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else {
				if (RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel()
						.equals(rootElementName)) {
					theModal = new OverallMortalityModal(shell, dataFile
							.getAbsolutePath(), savedFile.getAbsolutePath(),
							rootElementName, node);
				} else {
					if (RootElementNamesEnum.NEWBORNS.getNodeLabel().equals(
							rootElementName)) {
						theModal = new NewbornsModal(shell, dataFile
								.getAbsolutePath(),
								savedFile.getAbsolutePath(), rootElementName,
								node);
					} else {
						if (RootElementNamesEnum.OVERALLDALYWEIGHTS
								.getNodeLabel().equals(rootElementName)) {
							theModal = new OverallDALYWeightsModal(shell,
									dataFile.getAbsolutePath(), savedFile
											.getAbsolutePath(),
									rootElementName, node);
						} else {
							if (RootElementNamesEnum.DISEASEINCIDENCES
									.getNodeLabel().equals(rootElementName)) {
								theModal = new DiseaseIncidencesModal(shell,
										dataFile.getAbsolutePath(), savedFile
												.getAbsolutePath(),
										rootElementName, node);
							} else {
								if (RootElementNamesEnum.DISEASEPREVALENCES
										.getNodeLabel().equals(rootElementName)) {
									theModal = new DiseasePrevalencesModal(
											shell, dataFile.getAbsolutePath(),
											savedFile.getAbsolutePath(),
											rootElementName, node);
								} else {
									if (RootElementNamesEnum.DALYWEIGHTS
											.getNodeLabel().equalsIgnoreCase(
													rootElementName)) {
										theModal = new DALYWeightsModal(shell,
												dataFile.getAbsolutePath(),
												savedFile.getAbsolutePath(),
												rootElementName, node);
									} else {
										if (RootElementNamesEnum.EXCESSMORTALITY
												.getNodeLabel()
												.equalsIgnoreCase(
														rootElementName)) {
											theModal = new ExcessMortalityModal(
													shell,
													dataFile.getAbsolutePath(),
													savedFile.getAbsolutePath(),
													rootElementName, node);
										} else {

											if (RootElementNamesEnum.RELATIVERISKSFROMDISEASE
													.getNodeLabel()
													.equalsIgnoreCase(
															rootElementName)) {
												theModal = new RelRiskFromOtherDiseaseModal(
														shell,
														dataFile
																.getAbsolutePath(),
														savedFile
																.getAbsolutePath(),
														rootElementName, node,
														null);
											} else {
												if (RootElementNamesEnum.SIMULATION
														.getNodeLabel()
														.equals(rootElementName)) {
													if (simulationPreConditionsMet()) {
														theModal = new SimulationModal(
																shell,
																dataFile
																		.getAbsolutePath(),
																savedFile
																		.getAbsolutePath(),
																rootElementName,
																node, this.configurationFileExists);
													}
												} else {
													// RiskFactorConfigurations.
													if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL
															.getNodeLabel()
															.equals(
																	rootElementName)) {
														// Passed
														// numberOfCategories is
														// not used here, it is
														// derived from the
														// contents of the file.
														theModal = new RiskFactorCategoricalModal(
																shell,
																dataFile
																		.getAbsolutePath(),
																savedFile
																		.getAbsolutePath(),
																rootElementName,
																node, -1);
													} else {
														if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
																.getNodeLabel()
																.equals(
																		rootElementName)) {
															// Here the
															// selectedNumberOfCutoffs
															// is not
															// nescessary, it
															// will be
															// determined from
															// the file.
															theModal = new RiskFactorContinuousModal(
																	shell,
																	dataFile
																			.getAbsolutePath(),
																	savedFile
																			.getAbsolutePath(),
																	rootElementName,
																	node, -1);
														} else {
															if (RootElementNamesEnum.RISKFACTOR_COMPOUND
																	.getNodeLabel()
																	.equals(
																			rootElementName)) {
																theModal = new RiskFactorCompoundModal(
																		shell,
																		dataFile
																				.getAbsolutePath(),
																		savedFile
																				.getAbsolutePath(),
																		rootElementName,
																		node);
															} else {
																if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
																		.getNodeLabel()
																		.equals(
																				rootElementName)) {
																	theModal = new RelRiskFromRiskFactorCategoricalModal(
																			shell,
																			dataFile
																					.getAbsolutePath(),
																			savedFile
																					.getAbsolutePath(),
																			rootElementName,
																			node,
																			null);
																} else {
																	if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
																			.getNodeLabel()
																			.equals(
																					rootElementName)) {
																		theModal = new RelRiskFromRiskFactorContinuousModal(
																				shell,
																				dataFile
																						.getAbsolutePath(),
																				savedFile
																						.getAbsolutePath(),
																				rootElementName,
																				node,
																				null);
																	} else {
																		if (RootElementNamesEnum.RISKFACTOR_COMPOUND
																				.getNodeLabel()
																				.equals(
																						rootElementName)) {
																			theModal = new RiskFactorCompoundModal(
																					shell,
																					dataFile
																							.getAbsolutePath(),
																					savedFile
																							.getAbsolutePath(),
																					rootElementName,
																					node);
																		} else {
																			if (RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
																					.getNodeLabel()
																					.equals(
																							rootElementName)) {
																				theModal = new RiskFactorCategoricalPrevalencesModal(
																						shell,
																						dataFile
																								.getAbsolutePath(),
																						savedFile
																								.getAbsolutePath(),
																						rootElementName,
																						node);
																			} else {
																				if (RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
																						.getNodeLabel()
																						.equals(
																								rootElementName)) {
																					theModal = new RiskFactorContinuousPrevalencesModal(
																							shell,
																							dataFile
																									.getAbsolutePath(),
																							savedFile
																									.getAbsolutePath(),
																							rootElementName,
																							node);
																				} else {
																					if (RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
																							.getNodeLabel()
																							.equals(
																									rootElementName)) {
																						theModal = new RelRiskForDeathCategoricalModal(
																								shell,
																								dataFile
																										.getAbsolutePath(),
																								savedFile
																										.getAbsolutePath(),
																								rootElementName,
																								node);
																					} else {
																						if (RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
																								.getNodeLabel()
																								.equals(
																										rootElementName)) {
																							theModal = new RelRiskForDeathContinuousModal(
																									shell,
																									dataFile
																											.getAbsolutePath(),
																									savedFile
																											.getAbsolutePath(),
																									rootElementName,
																									node);
																						} else {
																							if (RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND
																									.getNodeLabel()
																									.equals(
																											rootElementName)) {
																								theModal = new RelRiskForDeathCompoundModal(
																										shell,
																										dataFile
																												.getAbsolutePath(),
																										savedFile
																												.getAbsolutePath(),
																										rootElementName,
																										node);
																							} else {
																								if (RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
																										.getNodeLabel()
																										.equals(
																												rootElementName)) {
																									theModal = new RelRiskForDisabilityCategoricalModal(
																											shell,
																											dataFile
																													.getAbsolutePath(),
																											savedFile
																													.getAbsolutePath(),
																											rootElementName,
																											node);
																								} else {
																									if (RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
																											.getNodeLabel()
																											.equals(
																													rootElementName)) {
																										theModal = new RelRiskForDisabilityContinuousModal(
																												shell,
																												dataFile
																														.getAbsolutePath(),
																												savedFile
																														.getAbsolutePath(),
																												rootElementName,
																												node);
																									} else {
																										if (RootElementNamesEnum.TRANSITIONMATRIX
																												.getNodeLabel()
																												.equals(
																														rootElementName)) {
																											theModal = new TransitionMatrixModal(
																													shell,
																													dataFile
																															.getAbsolutePath(),
																													savedFile
																															.getAbsolutePath(),
																													rootElementName,
																													node);
																										} else {
																											if (RootElementNamesEnum.TRANSITIONDRIFT
																													.getNodeLabel()
																													.equals(
																															rootElementName)) {
																												theModal = new TransitionDriftModal(
																														shell,
																														dataFile
																																.getAbsolutePath(),
																														savedFile
																																.getAbsolutePath(),
																														rootElementName,
																														node);
																											} else {
																												if (RootElementNamesEnum.TRANSITIONDRIFT_NETTO
																														.getNodeLabel()
																														.equals(
																																rootElementName)) {
																													theModal = new TransitionDriftNettoModal(
																															shell,
																															dataFile
																																	.getAbsolutePath(),
																															savedFile
																																	.getAbsolutePath(),
																															rootElementName,
																															node);
																												} else {
																													throw new DynamoConfigurationException(
																															"RootElementName "
																																	+ rootElementName
																																	+ " not implemented yet.");
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
				}
			}
			if (theModal != null) {
				Realm.runWithDefault(SWTObservables.getRealm(Display
						.getDefault()), theModal);
				boolean isPresentAfter = savedFile.exists();
				if (isPresentAfter && !isOld) {
					((ParentNode) node).addChild((ChildNode) new FileNode(
							(ParentNode) node, savedFile));
				}
				theViewer.refresh();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + savedFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}

	/**
	 * Method for checking the preconditions for making a new simulation
	 * configuration. When this check fails a messagebox indicating the error(s)
	 * is shown and false is returned.
	 * 
	 * @return true when creating a new simulation-configuration can go ahead.
	 *         false when no further action should be taken.
	 * @throws ConfigurationException
	 */
	private boolean simulationPreConditionsMet() throws ConfigurationException {
		boolean allTestsOK = true;
		StringBuffer errorMessage = new StringBuffer(
				"Not all preconditions have been met:\n");
		TreeAsDropdownLists instance = TreeAsDropdownLists.getInstance(node);
		instance.refresh(node);
		int numberOfPopulations = instance.getPopulations().size();
		if (numberOfPopulations == 0) {
			allTestsOK = false;
			errorMessage.append("No valid population was found.\n");
		}
		int numberOfDiseases = instance.getValidDiseases().size();
		if (numberOfDiseases == 0) {
			allTestsOK = false;
			errorMessage.append("No valid disease was found.\n");
		}
		int numberOfRiskFactors = instance.getRiskFactors().size();
		if (numberOfRiskFactors == 0) {
			allTestsOK = false;
			errorMessage.append("No valid risk factor was found.\n");
		}
		if (!allTestsOK) {
			MessageBox errorMessageBox = new MessageBox(shell,
					SWT.ERROR_NULL_ARGUMENT);
			errorMessageBox.setMessage(errorMessage.toString());
			errorMessageBox.open();
		}
		return allTestsOK;
	}

	public void setConfigurationFileExists(boolean configurationFileExists) {
		this.configurationFileExists = configurationFileExists;		
	}
}
