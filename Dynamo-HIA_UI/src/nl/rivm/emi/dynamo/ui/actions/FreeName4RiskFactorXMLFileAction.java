package nl.rivm.emi.dynamo.ui.actions;

// TODO:IMPORT

/**
 * Develop with populationSize as concrete implementation.
 * 
 * 20090115 RLM Refactored to use the RootElementNamesEnum.
 */
import java.io.File;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.ui.dialogs.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.util.RiskFactorStringConstantsEnum;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class FreeName4RiskFactorXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	RiskFactorStringConstantsEnum communicationEnum;

	public FreeName4RiskFactorXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, RiskFactorStringConstantsEnum communicationEnum) {
		super(shell, v, node, communicationEnum.getMyPR());
		this.communicationEnum = communicationEnum;
	}

	@Override
	/*
	 * 20090923 RLM Removed else for Compound Riskfactors, because the block
	 * code was identical.
	 */
	public void run() {
		try {
			if (node instanceof DirectoryNode) {
				String selectionPath = node.getPhysicalStorage()
						.getAbsolutePath();
				String rootElementName = ConfigurationFileUtil
						.extractRootElementNameFromSiblingConfiguration(node);
				log.debug(super.getText() + " for rootelement: "
						+ rootElementName);

				/**
				 * 
				 * TODO: REMOVE FOR VERSION 1.1
				 * 
				 * This condition is created to filter out
				 * RootElementNamesEnum.RISKFACTOR_COMPOUND and
				 * RootElementNamesEnum.RISKFACTOR_CONTINUOUS in version 1.0
				 * 
				 */
				if ((RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName))
						|| (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
								.getNodeLabel().equals(rootElementName))
						|| (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(rootElementName))) {
					ImportExtendedInputTrialog inputDialog = new ImportExtendedInputTrialog(
							shell, "Create file in the selected directory: "
									+ selectionPath, "Enter name for new "
									+ abstractName, "Name",
							new FileAndDirectoryNameInputValidator());
					inputDialog.open();
					int returnCode = inputDialog.getReturnCode();
					log.fatal("ReturnCode is: " + returnCode);
					if (returnCode != Window.CANCEL) {
						String candidateName = inputDialog.getValue();
						String candidatePath = selectionPath + File.separator
								+ candidateName + ".xml";
						File candidateFile = new File(candidatePath);
						if (candidateFile != null
								&& !candidateFile.getName().isEmpty()) {
							if (candidateFile.exists()) {
								MessageBox alreadyExistsMessageBox = new MessageBox(
										shell, SWT.ERROR_ITEM_NOT_ADDED);
								alreadyExistsMessageBox.setMessage("\""
										+ candidatePath
										+ "\"\n exists already.");
								alreadyExistsMessageBox.open();
							} else {
								String newPath = null;
								newPath = candidateFile.getAbsolutePath();
								File savedFile = new File(newPath);
								File dataFile = null;
								// Supply the location of dataFile
								if (returnCode == ImportExtendedInputTrialog.IMPORT_ID) {
									dataFile = this.getImportFile();
								} else {
									dataFile = savedFile;
								}
								processThroughModal(dataFile, savedFile,
										rootElementName);
							}
						}
					}
				} else {
					MessageBox messageBox = new MessageBox(shell);
					messageBox
							.setMessage("The functionality for rootelementname: \""
									+ rootElementName
									+ "\" has not been implemented.");
					messageBox.open();
				}

			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("\"" + this.getClass().getName()
						+ "\n\" should not be called on " + "\""
						+ node.getPhysicalStorage().getName() + "\"");
				messageBox.open();
			}
		} catch (TreeStructureException tse) {
			ErrorMessageUtil.showErrorMessage(this.log, this.shell, tse,
					"Could not extract rootelementname.",
					SWT.ERROR_UNSUPPORTED_DEPTH);
		} catch (DynamoConfigurationException dce) {
			ErrorMessageUtil
					.showErrorMessage(this.log, this.shell, dce,
							"Could not extract rootelementname.",
							SWT.ERROR_UNSPECIFIED);
		}
	}

	private void processThroughModal(File dataFile, File savedFile,
			String rootElementName) {
		try {
			FileCreationFlag.isOld = savedFile.exists();
			Runnable theModal = null;
			if (rootElementName == null) {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_NULL_ARGUMENT);
				messageBox.setMessage("No rootelementname selected.");
				messageBox.open();
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName)) {
					if (communicationEnum
							.equals(RiskFactorStringConstantsEnum.RISKFACTORPREVALENCES)) {
						theModal = new RiskFactorCategoricalPrevalencesModal(
								shell,
								dataFile.getAbsolutePath(),
								savedFile.getAbsolutePath(),
								RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
										.getNodeLabel(), node);
					} else {
						if (communicationEnum
								.equals(RiskFactorStringConstantsEnum.RISKFACTORTRANSITIONS)) {
							theModal = new TransitionMatrixModal(shell,
									dataFile.getAbsolutePath(), savedFile
											.getAbsolutePath(),
									RootElementNamesEnum.TRANSITIONMATRIX
											.getNodeLabel(), node);
						} else {
							if (communicationEnum
									.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDEATH)) {
								theModal = new RelRiskForDeathCategoricalModal(
										shell,
										dataFile.getAbsolutePath(),
										savedFile.getAbsolutePath(),
										RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
												.getNodeLabel(), node);
							} else {
								if (communicationEnum
										.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDISABILITY)) {
									theModal = new RelRiskForDisabilityCategoricalModal(
											shell,
											dataFile.getAbsolutePath(),
											savedFile.getAbsolutePath(),
											RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
													.getNodeLabel(), node);
								} else {
									MessageBox messageBox = new MessageBox(
											shell, SWT.ERROR_UNSUPPORTED_FORMAT);
									messageBox.setMessage("\""
											+ communicationEnum.name()
											+ "\" not supported.");
									messageBox.open();
								}
							}
						}
					}
				} else {
					if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
							.getNodeLabel().equals(rootElementName)) {
						if (communicationEnum
								.equals(RiskFactorStringConstantsEnum.RISKFACTORPREVALENCES)) {
							theModal = new RiskFactorContinuousPrevalencesModal(
									shell,
									dataFile.getAbsolutePath(),
									savedFile.getAbsolutePath(),
									RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
											.getNodeLabel(), node);
						} else {
							if (communicationEnum
									.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDEATH)) {
								theModal = new RelRiskForDeathContinuousModal(
										shell,
										dataFile.getAbsolutePath(),
										savedFile.getAbsolutePath(),
										RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
												.getNodeLabel(), node);
							} else {
								if (communicationEnum
										.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDISABILITY)) {
									theModal = new RelRiskForDisabilityContinuousModal(
											shell,
											dataFile.getAbsolutePath(),
											savedFile.getAbsolutePath(),
											RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
													.getNodeLabel(), node);
								} else {
									MessageBox messageBox = new MessageBox(
											shell, SWT.ERROR_NOT_IMPLEMENTED);
									messageBox.setMessage("\""
											+ rootElementName
											+ "\" not yet implemented.");
									messageBox.open();
								}
							}
						}
					} else {
						if (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(rootElementName)) {
							// TODO Implement getting category-int-s.
							//int numberOfCategories = 3;
							//int durationCategoryIndex = 3;
							if (communicationEnum
									.equals(RiskFactorStringConstantsEnum.RISKFACTORPREVALENCES)) {
								theModal = new RiskFactorCompoundPrevalencesModal(
										shell,
										dataFile.getAbsolutePath(),
										savedFile.getAbsolutePath(),
										RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
												.getNodeLabel(), node);
							} else {
								if (communicationEnum
										.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDEATH)) {
									theModal = new RelRiskForDeathCompoundModal(
											shell,
											dataFile.getAbsolutePath(),
											savedFile.getAbsolutePath(),
											RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND
													.getNodeLabel(), node);
								} else {
									if (communicationEnum
											.equals(RiskFactorStringConstantsEnum.RISKFACTORRELATIVERISKSFORDISABILITY)) {
										theModal = new RelRiskForDisabilityCompoundModal(
												shell,
												dataFile.getAbsolutePath(),
												savedFile.getAbsolutePath(),
												RootElementNamesEnum.RELATIVERISKSFORDISABILITY_COMPOUND
														.getNodeLabel(), node);
									} else {
										MessageBox messageBox = new MessageBox(
												shell,
												SWT.ERROR_NOT_IMPLEMENTED);
										messageBox.setMessage("\""
												+ rootElementName
												+ "\" not yet implemented.");
										messageBox.open();
									}
								}
							}
						} else {
							MessageBox messageBox = new MessageBox(shell,
									SWT.ERROR_UNSUPPORTED_FORMAT);
							messageBox.setMessage("\"" + rootElementName
									+ "\" not supported.");
							messageBox.open();
						}
					}
				}
				Realm.runWithDefault(SWTObservables.getRealm(Display
						.getDefault()), theModal);
				boolean isPresentAfter = savedFile.exists();
				if (isPresentAfter && !FileCreationFlag.isOld) {
					((ParentNode) node).addChild((ChildNode) new FileNode(
							(ParentNode) node, savedFile));
					FileCreationFlag.isOld = true;
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
	 * @return File The selected import file
	 */
	public File getImportFile() {
		FileDialog fileDialog = new FileDialog(this.shell);
		fileDialog.open();
		return new File(fileDialog.getFilterPath() + File.separator
				+ fileDialog.getFileName());
	}

}
