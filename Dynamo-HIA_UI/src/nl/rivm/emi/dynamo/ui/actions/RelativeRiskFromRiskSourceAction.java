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
import nl.rivm.emi.dynamo.ui.dialogs.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.dialogs.TransitionTrialog;
import nl.rivm.emi.dynamo.ui.main.DropDownTrialog;
import nl.rivm.emi.dynamo.ui.main.FreeNamePlusDropDownModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromOtherDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RelativeRiskFromRiskSourceAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	String rootElementName = null;
	String riskSourceRootElementName = null;

	public RelativeRiskFromRiskSourceAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, "aBSTRACT");
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		if (node instanceof DirectoryNode) {
			FreeNamePlusDropDownModal theModal = new FreeNamePlusDropDownModal(
					shell, node);
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					theModal);
			String candidatePath = theModal.getNewFilePath();
			
			if(candidatePath != null){
			File candidateFile = new File(candidatePath);
			if (candidateFile != null && !candidateFile.getName().isEmpty()) {
				if (candidateFile.exists()) {
					MessageBox alreadyExistsMessageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					alreadyExistsMessageBox.setMessage("\"" + candidatePath
							+ "\"\n exists already.");
					alreadyExistsMessageBox.open();
				} else {
					File dataFile;
					String importFilePath = theModal.getDataFilePath();				
					if (importFilePath == null) {
						dataFile = candidateFile;
					} else {
						dataFile = new File(importFilePath);
					}
					processThroughModal(dataFile, candidateFile, theModal.getRsProps());
				}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("No new file could be created.");
				messageBox.open();
			}
			} 
// This was already signalled to the user. 
//			else {
//				MessageBox messageBox = new MessageBox(shell);
//				messageBox.setMessage("No valid filename created.");
//				messageBox.open();
//			}			
		} else {
			MessageBox messageBox = new MessageBox(shell);
			messageBox.setMessage("\"" + this.getClass().getName()
					+ "\n\" should not be called on " + "\""
					+ node.getPhysicalStorage().getName() + "\"");
			messageBox.open();
		}
	}


	/**
	 * Opens the transitions trialog and retrieves the xml file name, either as
	 * new name or imported
	 * 
	 * @return String the fileName
	 */
/* 	@Override
	protected String getNewFilePath() {
		String selectionPath = node.getPhysicalStorage().getAbsolutePath();
		String newPath = null;
		// Util.deriveEntityLabelAndValueFromRiskSourceNode(this.node)[0]
		// Call the input trialog modal here (trialog includes input field,
		// import, ok and cancel buttons)
		DropDownTrialog inputDialog = new DropDownTrialog(shell,
				"BasePath: " + selectionPath,
				"Enter name for a new relative risks file", "Name",
				new FileAndDirectoryNameInputValidator(), this.node);

		// /TODO Use
		// Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode)
		// to set the RF Name, create a new method to show RF type

		int openValue = inputDialog.open();

		log.debug("OpenValue is: " + openValue);

		int returnCode = inputDialog.getReturnCode();

		this.bulletButtonName = inputDialog.getSelectedBulletButtonName();
		log.debug("bulletButtonName: " + this.bulletButtonName);

		log.debug("ReturnCode is: " + returnCode);

		if (returnCode != Window.CANCEL) {
			String candidateName = inputDialog.getValue();
			String candidatePath = selectionPath + File.separator
					+ candidateName + ".xml";
			File candidateFile = new File(candidatePath);
			if (!candidateFile.exists()/* && candidateFile.createNewFile() *//*) {
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
				processThroughModal(dataFile, savedFile);
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
*/
	private void processThroughModal(File dataFile, File candidateFile, RiskSourceProperties props) {
		try {
			boolean isOld = candidateFile.exists();
			Runnable theModal = null;
			String chosenRootElementName = props.getRootElementName();
			if (chosenRootElementName == null) {
					theModal = new RelRiskFromOtherDiseaseModal(shell, dataFile
							.getAbsolutePath(), candidateFile.getAbsolutePath(), rootElementName,
							node, props);
			} else {
				fillRootElementName(chosenRootElementName);
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL
						.getNodeLabel().equals(chosenRootElementName)) {
					theModal = new RelRiskFromRiskFactorCategoricalModal(
							shell, dataFile.getAbsolutePath(), candidateFile.getAbsolutePath(),
							rootElementName, node, props);
				} else {
					if (RootElementNamesEnum.RISKFACTOR_COMPOUND
							.getNodeLabel().equals(chosenRootElementName)) {
						theModal = null;
					} else {
						if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
								.getNodeLabel().equals(
										chosenRootElementName)) {
							theModal = new RelRiskFromRiskFactorContinuousModal(
									shell, dataFile.getAbsolutePath(), candidateFile.getAbsolutePath(),
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
