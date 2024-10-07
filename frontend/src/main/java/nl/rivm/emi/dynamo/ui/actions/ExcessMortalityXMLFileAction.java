package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.ui.dialogs.ExcessMortalityTrialog;
import nl.rivm.emi.dynamo.ui.dialogs.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.dialogs.TransitionTrialog;
import nl.rivm.emi.dynamo.ui.main.DataLessMessageModal;
import nl.rivm.emi.dynamo.ui.main.ExcessMortalityModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.eclipse.core.databinding.observable.Realm;
//ND: Use DisplayRealm instead of SWTObservables
import org.eclipse.jface.databinding.swt.DisplayRealm;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ExcessMortalityXMLFileAction extends FreeNameXMLFileAction {

	private String bulletButtonName;
	private String diseaseName;

	public ExcessMortalityXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, rootElementName,
				new FileAndDirectoryNameInputValidator());
		this.diseaseName = ((BaseNode) ((ChildNode) node).getParent())
				.deriveNodeLabel();
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
		ExcessMortalityTrialog inputDialog = new ExcessMortalityTrialog(shell,
				"BasePath: " + selectionPath,
				"Enter name for the new excess mortality file", "Name",
				new FileAndDirectoryNameInputValidator(), this.diseaseName,
				rootElementName);
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

	@Override
	protected void processThroughModal(File dataFile, File savedFile) {
		try {
			FileCreationFlag.isOld = savedFile.exists();
			Runnable theModal = null;
			log.debug("rootElementName: " + rootElementName);
			log.debug("this.bulletButtonName: " + this.bulletButtonName);
			if (RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel().equals(
					rootElementName)) {
				theModal = new ExcessMortalityModal(shell, dataFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, this.bulletButtonName);
			} else {
				throw new DynamoConfigurationException(
						"Unexpected rootElementName " + rootElementName);
			}
			Realm.runWithDefault(DisplayRealm.getRealm(Display.getDefault()),
					theModal);
			boolean isPresentAfter = savedFile.exists();
			if (isPresentAfter && !FileCreationFlag.isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, savedFile));
				FileCreationFlag.isOld = true;
			}
			theViewer.refresh();
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
