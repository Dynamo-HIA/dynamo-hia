package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.dialogs.DurationDistributionTrialog;
import nl.rivm.emi.dynamo.ui.dialogs.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.DurationDistributionModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DurationDistributionFreeXMLFilePlusTypeBulletsAction extends
		FreeNameXMLFileAction {

	private String bulletButtonName;
	private String riskFactorName;
	private String riskFactorType;

	public DurationDistributionFreeXMLFilePlusTypeBulletsAction(Shell shell,
			TreeViewer v, BaseNode node, String rootElementName,
			String riskFactorName, String riskFactorType) {
		super(shell, v, node, rootElementName, null);
		this.riskFactorName = riskFactorName;
		this.riskFactorType = riskFactorType;
	}

	/**
	 * Opens the transitions trialog and retrieves the xml file name, either as
	 * new name or imported
	 * 
	 * @return String the fileName
	 */
	@Override
	protected String getNewFilePath() {
		String selectionPath = node.getPhysicalStorage().getAbsolutePath();
		String newPath = null;
		// Util.deriveEntityLabelAndValueFromRiskSourceNode(this.node)[0]
		// Call the input trialog modal here (trialog includes input field,
		// import, ok and cancel buttons)
		DurationDistributionTrialog inputDialog = new DurationDistributionTrialog(
				shell, "BasePath: " + selectionPath,
				"Enter name for the new duration distribution file:", "Name",
				new FileAndDirectoryNameInputValidator(), this.riskFactorName,
				this.riskFactorType);

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
				String selectedRootElementName = inputDialog
						.getSelectedRootElementName();
				processThroughModal(dataFile, savedFile,
						selectedRootElementName);
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

	protected void processThroughModal(File dataFile, File savedFile,
			String chosenRootElementName) {
		try {
			FileCreationFlag.isOld = savedFile.exists();
			Runnable theModal = null;
			log.debug("rootElementName" + chosenRootElementName);
			log.debug("this.bulletButtonName" + this.bulletButtonName);
			if (RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION
					.getNodeLabel().equals(chosenRootElementName)) {
				theModal = new DurationDistributionModal(shell, dataFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						chosenRootElementName, node);
			} else {
				final String requiredRootElementName = RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION_UNIFORM
						.getNodeLabel();
				if (requiredRootElementName.equals(chosenRootElementName)) {
					// theModal = new TransitionDriftNettoModal(shell,
					// dataFile.getAbsolutePath(), savedFile
					// .getAbsolutePath(), this.bulletButtonName, node);
					MessageBox messageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					messageBox.setMessage("RootElementName: \""
							+ chosenRootElementName
							+ "\" has not been implemented yet.");
					messageBox.open();
				} else {
					throw new DynamoConfigurationException(
							"Unexpected rootElementName "
									+ chosenRootElementName);
				}
			}
			if (theModal != null) {
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

}
