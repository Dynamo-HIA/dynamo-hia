package nl.rivm.emi.dynamo.ui.actions;

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
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class FreeNameXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;

	public FreeNameXMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		if (node instanceof DirectoryNode) {
			String newFilePath = getNewFilePath();
			if (newFilePath != null) {
				String filePath = "";
				filePath = newFilePath;
				File file = new File(filePath);
				processThroughModal(file);
			} else {
				return;
			}
		} else {
			MessageBox messageBox = new MessageBox(shell);
			messageBox.setMessage("\"" + this.getClass().getName()
					+ "\n\" should not be called on " + "\""
					+ node.getPhysicalStorage().getName() + "\"");
			messageBox.open();
		}
	}

	private String getNewFilePath() {
		String selectionPath = node.getPhysicalStorage().getAbsolutePath();
		String newPath = null;
		InputDialog inputDialog = new InputDialog(shell, "BasePath: "
				+ selectionPath, "Enter name for new file", "Name", null);
		inputDialog.open();
		int returnCode = inputDialog.getReturnCode();
		log.debug("ReturnCode is: " + returnCode);
		if (returnCode != Window.CANCEL) {
			String candidateName = inputDialog.getValue();
			String candidatePath = selectionPath + File.separator
					+ candidateName + ".xml";
			File candidateFile = new File(candidatePath);
			if (!candidateFile.exists()/* && candidateFile.createNewFile() */) {
				newPath = candidateFile.getAbsolutePath();
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

	private void processThroughModal(File file) {
		try {
			boolean isOld = file.exists();
			Runnable theModal = null;
			if (RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel().equals(rootElementName)) {
				theModal = new DiseaseIncidencesModal(shell, file
						.getAbsolutePath(), rootElementName, node);
			} else {
				if (RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel().equals(rootElementName)) {
					theModal = new DiseasePrevalencesModal(shell, file
							.getAbsolutePath(), rootElementName, node);
				} else {
					if (RootElementNamesEnum.SIMULATION.getNodeLabel().equals(rootElementName)) {
						theModal = new SimulationModal(shell, file
								.getAbsolutePath(), rootElementName, node);
					} else {
						if (RootElementNamesEnum.DALYWEIGHTS.getNodeLabel().equals(rootElementName)) {
							theModal = new DALYWeightsModal(shell, file
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
			boolean isPresentAfter = file.exists();
			if (isPresentAfter && !isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, file));
			}
			theViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + file.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}
}
