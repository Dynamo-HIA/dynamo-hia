package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.DiseaseIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.DiseasePrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.main.TransitionTrialog;
import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class InputBulletsFreeXMLFileAction extends FreeNameXMLFileAction {

	private String bulletButtonName;
	private String riskFactorName;
	private String riskFactorType;

	public InputBulletsFreeXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName, String riskFactorName, 
			String riskFactorType) {
		super(shell, v, node, rootElementName);
		this.riskFactorName = riskFactorName;
		this.riskFactorType = riskFactorType;
	}
	
	/**
	 * Opens the transitions trialog
	 * and retrieves the xml file name,
	 * either as new name or imported
	 * 
	 * @return String the fileName
	 */
	@Override
	protected String getNewFilePath() {
		String selectionPath = node.getPhysicalStorage().getAbsolutePath();
		String newPath = null;
		//Util.deriveEntityLabelAndValueFromRiskSourceNode(this.node)[0]
		// Call the input trialog modal here (trialog includes input field, import, ok and cancel buttons)
		TransitionTrialog inputDialog = new TransitionTrialog(shell, "BasePath: "
				+ selectionPath, "Enter name for a new xml file", "Name", null, 
				this.riskFactorName, this.riskFactorType
				);
		
		///TODO Use Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode)
		//to set the RF Name, create a new method to show RF type
		
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
			boolean isOld = savedFile.exists();
			Runnable theModal = null;
			log.debug("rootElementName" + rootElementName);
			log.debug("this.bulletButtonName" + this.bulletButtonName);
			if (RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel().equals(rootElementName) && 
					BulletButtonNamesEnum.USER_SPECIFIED.getBulletButtonName().equals(this.bulletButtonName)) {
				theModal = new TransitionDriftModal(shell, dataFile.getAbsolutePath(), savedFile
						.getAbsolutePath(), rootElementName, node);
			} else {	
				if (RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel().equals(rootElementName) && 
						BulletButtonNamesEnum.ZERO.getBulletButtonName().equals(this.bulletButtonName)) {
					theModal = new TransitionDriftModal(shell, dataFile.getAbsolutePath(), savedFile
							.getAbsolutePath(), rootElementName, node);
				} else {						
					if (RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel().equals(rootElementName) && 
							BulletButtonNamesEnum.NETTO.getBulletButtonName().equals(this.bulletButtonName)) {
						theModal = new TransitionDriftModal(shell, dataFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
					} else {						
						/* TODO
						if (RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel().equals(rootElementName)) {
							theModal = new TransitionMatrixModal(shell, dataFile.getAbsolutePath(), savedFile
									.getAbsolutePath(), rootElementName, node);
						} else {*/													
									throw new DynamoConfigurationException(
									"RootElementName " + rootElementName
											+ " not implemented yet.");
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
	
	
}
