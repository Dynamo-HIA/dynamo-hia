package nl.rivm.emi.dynamo.ui.actions;
// TODO:IMPORT

/**
 * Develop with populationSize as concrete implementation.
 * 
 * 20090115 RLM Refactored to use the RootElementNamesEnum.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.DiseaseIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.DiseasePrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

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

public class FreeNameXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	protected String rootElementName;

	public FreeNameXMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		if (node instanceof DirectoryNode) {
			String newFilePath = getNewFilePath();
		} else {
			MessageBox messageBox = new MessageBox(shell);
			messageBox.setMessage("\"" + this.getClass().getName()
					+ "\n\" should not be called on " + "\""
					+ node.getPhysicalStorage().getName() + "\"");
			messageBox.open();
		}
	}

	protected String getNewFilePath() {
		String selectionPath = node.getPhysicalStorage().getAbsolutePath();
		String newPath = null;
		// Call the input trialog modal here (trialog includes input field, import, ok and cancel buttons)
		ImportExtendedInputTrialog inputDialog = new ImportExtendedInputTrialog(shell, "BasePath: "
				+ selectionPath, "Enter name for a new xml file", "Name", null);
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

	protected void processThroughModal(File dataFile, File savedFile) {
		try {
			boolean isOld = savedFile.exists();
			Runnable theModal = null;
			if (RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel().equals(rootElementName)) {
				theModal = new DiseaseIncidencesModal(shell, dataFile.getAbsolutePath(), savedFile
						.getAbsolutePath(), rootElementName, node);
			} else {
				if (RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel().equals(rootElementName)) {
					theModal = new DiseasePrevalencesModal(shell, dataFile.getAbsolutePath(), savedFile
							.getAbsolutePath(), rootElementName, node);
				} else {
					if (RootElementNamesEnum.SIMULATION.getNodeLabel().equals(rootElementName)) {
						theModal = new SimulationModal(shell, dataFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
					} else {
						if (RootElementNamesEnum.DALYWEIGHTS.getNodeLabel().equals(rootElementName)) {
							theModal = new DALYWeightsModal(shell, dataFile.getAbsolutePath(), savedFile
									.getAbsolutePath(), rootElementName, node);
						} else {					
							throw new DynamoConfigurationException(
							"RootElementName " + rootElementName
									+ " not implemented yet.");
						}
					}
				}
			}
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					theModal);
			boolean isPresentAfter = savedFile.exists();
			if (isPresentAfter && !isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, savedFile));
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
	
	/**
	 * @return File The selected import file
	 */
	public File getImportFile() {
		FileDialog fileDialog = new FileDialog(this.shell);
		fileDialog.open();		
		return new File(fileDialog.getFilterPath()
				+ File.separator + fileDialog.getFileName());
	}	
	
}
