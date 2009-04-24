package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorTypeBulletsModal;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorTypeBulletsAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorTypeBulletsAction(Shell shell, TreeViewer v, BaseNode node) {
		super(shell, v, node, "aBSTRACT");
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

			if (file != null && !file.getName().isEmpty()) {
				if (file.exists()) {
					MessageBox alreadyExistsMessageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					alreadyExistsMessageBox.setMessage("\"" + candidatePath
							+ "\"\n exists already.");
					alreadyExistsMessageBox.open();
				} else {
					processThroughModal(file, theModal);
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

	private void processThroughModal(File file, RiskFactorTypeBulletsModal previousModal) {
			String selectedRootElementName = previousModal.getSelectedRootElementName();
		log.debug("selectedRootElementNamexxx:" + selectedRootElementName);
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
					int selectedNumberOfClasses = previousModal.getNumberOfClasses();
					theModal = new RiskFactorCategoricalModal(shell, file
							.getAbsolutePath(), file.getAbsolutePath(),
							selectedRootElementName, node, selectedNumberOfClasses);
				} else {
					int selectedNumberOfCutoffs = previousModal.getNumberOfCutoffs();
					if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
							.getNodeLabel().equals(selectedRootElementName)) {
						theModal = new RiskFactorContinuousModal(shell, file
								.getAbsolutePath(), file.getAbsolutePath(),
								selectedRootElementName, node, selectedNumberOfCutoffs);
					} else {
						int selectedNumberOfCompoundClasses = previousModal.getNumberOfCompoundClasses();
                        // TODO(mondeelr) Add variable number of classes here.
						if (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(selectedRootElementName)) {
							theModal = new RiskFactorCompoundModal(shell, file
									.getAbsolutePath(), file.getAbsolutePath(),
									selectedRootElementName, node);
						} else {
							throw new DynamoConfigurationException(
									"RootElementName "
											+ selectedRootElementName
											+ " not implemented.");
						}
					}
				}
				Realm.runWithDefault(SWTObservables.getRealm(Display
						.getDefault()), theModal);
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
