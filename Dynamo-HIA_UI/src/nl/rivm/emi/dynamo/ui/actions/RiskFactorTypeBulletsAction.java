package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.FreeNamePlusDropDownModal;
import nl.rivm.emi.dynamo.ui.main.FreePlusDropDownAndTypeBulletsModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromOtherDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorTypeBulletsModal;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorTypeBulletsAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	String selectedRootElementName = null;

	public RiskFactorTypeBulletsAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, "aBSTRACT");
		this.selectedRootElementName = rootElementName;
	}

	@Override
	public void run() {
		if (node instanceof DirectoryNode) {
			String selectionPath = node.getPhysicalStorage().getAbsolutePath();
			RiskFactorTypeBulletsModal theModal = new RiskFactorTypeBulletsModal(
					shell, selectionPath, node);
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					theModal);
			String candidatePath = theModal.getNewFilePath();
			File file = new File(candidatePath);
			if (file != null) {
				if (file.exists()) {
					MessageBox alreadyExistsMessageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					alreadyExistsMessageBox.setMessage("\"" + candidatePath
							+ "\"\n exists already.");
					alreadyExistsMessageBox.open();
				} else {
					MessageBox messageBox = new MessageBox(shell);
					messageBox.setMessage(theModal.getSelectedRootElementName());
					messageBox.open();
					processThroughModal(file, theModal.getSelectedRootElementName());
				}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("No new file could be created.");
				messageBox.open();
			}
		} else {
			MessageBox messageBox = new MessageBox(shell);
			messageBox.setMessage("\"" + this.getClass().getName()
					+ "\n\" should not be called on " + "\""
					+ node.getPhysicalStorage().getName() + "\"");
			messageBox.open();
		}
	}

	private void processThroughModal(File file, String selectedRootElementName) {
		try {
			boolean isOld = file.exists();
			Runnable theModal = null;
			if (selectedRootElementName == null) {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_NULL_ARGUMENT);
				messageBox.setMessage("No rootelementname selected.");
				messageBox.open();
			} else {
			if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
					.equals(selectedRootElementName)) {
				theModal = new RiskFactorCategoricalModal(shell, file
						.getAbsolutePath(), selectedRootElementName, node);
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel()
						.equals(selectedRootElementName)) {
					theModal = null; // TODO
				} else {
					if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
							.equals(selectedRootElementName)) {
						theModal = null; // TODO
					} else {
						throw new DynamoConfigurationException(
								"RootElementName " + selectedRootElementName
										+ " not implemented.");
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
			}
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
