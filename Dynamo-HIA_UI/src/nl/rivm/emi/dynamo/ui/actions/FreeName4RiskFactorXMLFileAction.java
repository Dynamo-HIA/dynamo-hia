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
import nl.rivm.emi.dynamo.ui.main.ImportExtendedInputTrialog;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalPrevalencesModal;
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

public class FreeName4RiskFactorXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public FreeName4RiskFactorXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, "riskfactor prevalences file");
	}	
	
	@Override
	public void run() {
		try {
			if (node instanceof DirectoryNode) {
				String selectionPath = node.getPhysicalStorage()
				.getAbsolutePath();
				String rootElementName = ConfigurationFileUtil
						.extractRootElementNameFromSiblingConfiguration(node);
				log.debug("Creating prevalences file for rootelement: "
						+ rootElementName);
				
				/**
				 * 
				 * TODO: REMOVE FOR VERSION 1.1
				 * 
				 * This condition is created to filter out 
				 * RootElementNamesEnum.RISKFACTOR_COMPOUND and 
				 * RootElementNamesEnum.RISKFACTOR_CONTINUOUS
				 * in version 1.0
				 * 
				 */
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL
						.getNodeLabel().equals(rootElementName)) {					
					// Call the input trialog modal here (trialog includes input field,
					// import, ok and cancel buttons)
					ImportExtendedInputTrialog inputDialog = new ImportExtendedInputTrialog(
							shell, "Create file in the selected directory: " + selectionPath,
							"Enter name for new " + abstractName, "Name", null);										
					inputDialog.open();
					int returnCode = inputDialog.getReturnCode();
					log.fatal("ReturnCode is: " + returnCode);
					if (returnCode != Window.CANCEL) {
						String candidateName = inputDialog.getValue();
						String candidatePath = selectionPath + File.separator
								+ candidateName + ".xml";
						File candidateFile = new File(candidatePath);
						if (candidateFile != null && !candidateFile.getName().isEmpty()) {
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
								
								processThroughModal(dataFile, 
										savedFile, rootElementName);
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

	private void processThroughModal(File dataFile, File savedFile, String rootElementName) {
		try {
			boolean isOld = savedFile.exists();
			Runnable theModal = null;
			if (rootElementName == null) {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_NULL_ARGUMENT);
				messageBox.setMessage("No rootelementname selected.");
				messageBox.open();
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(rootElementName)) {
					theModal = new RiskFactorCategoricalPrevalencesModal(
							shell,
							dataFile.getAbsolutePath(),
							savedFile.getAbsolutePath(),
							RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
									.getNodeLabel(), node);
				} else {
					if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
							.getNodeLabel().equals(rootElementName)) {
						MessageBox messageBox = new MessageBox(shell,
								SWT.ERROR_NOT_IMPLEMENTED);
						messageBox.setMessage("\"" + rootElementName
								+ "\" not yet implemented.");
						messageBox.open();
					} else {
						if (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(rootElementName)) {
							MessageBox messageBox = new MessageBox(shell,
									SWT.ERROR_NOT_IMPLEMENTED);
							messageBox.setMessage("\"" + rootElementName
									+ "\" not yet implemented.");
							messageBox.open();
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
	 * @return File The selected import file
	 */
	public File getImportFile() {
		FileDialog fileDialog = new FileDialog(this.shell);
		fileDialog.open();
		return new File(fileDialog.getFilterPath() + File.separator
				+ fileDialog.getFileName());
	}
	
}
