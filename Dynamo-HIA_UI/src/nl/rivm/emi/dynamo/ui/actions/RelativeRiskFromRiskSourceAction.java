package nl.rivm.emi.dynamo.ui.actions;

/**
 * Menu action that can process both relative risks from disease and risk factor choices.
 * 
 * When disease is the risk source the rootelementname is known, for risk factors three 
 * rootelementnames are possible and dependent on the riskfactor chosen.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.dialogs.DropDownAndImportExtendedInputDialog;
import nl.rivm.emi.dynamo.ui.dialogs.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromOtherDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RelativeRiskFromRiskSourceAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	String rootElementName = null;
	String riskSourceRootElementName = null;
	protected IInputValidator theValidator = null;
	private String file2ImportFilePath = null;

	public RelativeRiskFromRiskSourceAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName, IInputValidator theValidator) {
		super(shell, v, node, "aBSTRACT");
		this.rootElementName = rootElementName;
		this.theValidator = theValidator;
	}

	@Override
	public void run() {
		if (node instanceof DirectoryNode) {
			// FreeNamePlusDropDownModal theModal = new
			// FreeNamePlusDropDownModal(
			// shell, node);
			// Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
			// theModal);
			String candidatePath = /* theModal. */getNewFilePath();
			/*
			 * if (candidatePath != null) { File candidateFile = new
			 * File(candidatePath); if (candidateFile != null &&
			 * !candidateFile.getName().isEmpty()) { if (candidateFile.exists())
			 * { MessageBox alreadyExistsMessageBox = new MessageBox( shell,
			 * SWT.ERROR_ITEM_NOT_ADDED);
			 * alreadyExistsMessageBox.setMessage("\"" + candidatePath +
			 * "\"\n exists already."); alreadyExistsMessageBox.open(); } else {
			 * File dataFile; String importFilePath =
			 * theModal.getDataFilePath(); if (importFilePath == null) {
			 * dataFile = candidateFile; } else { dataFile = new
			 * File(importFilePath); } processThroughModal(dataFile,
			 * candidateFile, theModal .getRsProps()); } } else { MessageBox
			 * messageBox = new MessageBox(shell);
			 * messageBox.setMessage("No new file could be created.");
			 * messageBox.open(); } } // This was already signalled to the user.
			 * // else { // MessageBox messageBox = new MessageBox(shell); //
			 * messageBox.setMessage("No valid filename created."); //
			 * messageBox.open(); // } } else { MessageBox messageBox = new
			 * MessageBox(shell); messageBox.setMessage("\"" +
			 * this.getClass().getName() + "\n\" should not be called on " +
			 * "\"" + node.getPhysicalStorage().getName() + "\"");
			 * messageBox.open();
			 */
		}

	}

	protected String getNewFilePath() {
			String selectionPath = node.getPhysicalStorage().getAbsolutePath();
			String newPath = null;
			// Call the input trialog modal here (trialog includes input field,
			// import, ok and cancel buttons)
			DropDownAndImportExtendedInputDialog inputDialog = new DropDownAndImportExtendedInputDialog(
					shell, node, "Enter name for a new xml file", "Name",
					theValidator);
			int openValue = inputDialog.open();

			log.debug("OpenValue is: " + openValue);

			int returnCode = inputDialog.getReturnCode();
			log.debug("ReturnCode is: " + returnCode);
			if (returnCode != Window.CANCEL) {
				String candidateName = inputDialog.getValue();
				String candidatePath = selectionPath + File.separator
						+ candidateName + ".xml";
				File candidateFile = new File(candidatePath);
				if (!candidateFile.exists()/* && candidateFile.createNewFile() */) {
					newPath = candidateFile.getAbsolutePath();
					File savedFile = new File(newPath);
					File dataFile = null;
					// Supply the location of dataFile
					if (returnCode == ImportExtendedInputTrialog.IMPORT_ID) {
						dataFile = this.getImportFile();
					} else {
						dataFile = savedFile;
					}

					// Process the modal
					RiskSourceProperties riskSourceProperties = inputDialog
							.getSelectedRiskSourceProperties();
					processThroughModal(dataFile, savedFile,
							riskSourceProperties);
				} else {
					MessageBox messageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					messageBox.setMessage("\"" + candidateName
							+ "\"\n exists already.");
					messageBox.open();
				}
			}
			return newPath;
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

	private void processThroughModal(File dataFile, File candidateFile,
			RiskSourceProperties props) {
		try {
			boolean isOld = candidateFile.exists();
			Runnable theModal = null;
			String chosenRootElementName = props.getRootElementName();
			if (chosenRootElementName == null) {
				theModal = new RelRiskFromOtherDiseaseModal(shell, dataFile
						.getAbsolutePath(), candidateFile.getAbsolutePath(),
						rootElementName, node, props);
			} else {
				fillRootElementName(chosenRootElementName);
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(chosenRootElementName)) {
					theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
							dataFile.getAbsolutePath(), candidateFile
									.getAbsolutePath(), rootElementName, node,
							props);
				} else {
					if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
							.equals(chosenRootElementName)) {
						theModal = null;
					} else {
						if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
								.getNodeLabel().equals(chosenRootElementName)) {
							theModal = new RelRiskFromRiskFactorContinuousModal(
									shell, dataFile.getAbsolutePath(),
									candidateFile.getAbsolutePath(),
									rootElementName, node, props);
						} else {
							throw new DynamoConfigurationException(
									"Unexpected RootElementName "
											+ chosenRootElementName + ".");
						}
					}
				}
			}
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					theModal);
			boolean isPresentAfter = candidateFile.exists();
			if (isPresentAfter && !isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, candidateFile));
			}
			theViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + candidateFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}

	private void fillRootElementName(String riskSourceRootElementName) {
		if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel().equals(
				riskSourceRootElementName)) {
			rootElementName = RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel();
		} else {
			if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel()
					.equals(riskSourceRootElementName)) {
				rootElementName = RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
						.getNodeLabel();
			} else {
				if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
						.equals(riskSourceRootElementName)) {
					rootElementName = RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND
							.getNodeLabel();
				}
			}
		}
	}
}
