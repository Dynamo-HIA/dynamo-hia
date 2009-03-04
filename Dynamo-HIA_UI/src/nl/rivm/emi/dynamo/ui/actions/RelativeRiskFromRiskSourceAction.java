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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
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
			File file = new File(candidatePath);
			if (file != null && !file.getName().isEmpty()) {
				if (file.exists()) {
					MessageBox alreadyExistsMessageBox = new MessageBox(shell,
							SWT.ERROR_ITEM_NOT_ADDED);
					alreadyExistsMessageBox.setMessage("\"" + candidatePath
							+ "\"\n exists already.");
					alreadyExistsMessageBox.open();
				} else {
					processThroughModal(file, theModal.getRsProps());
				}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("No new file could be created.");
				messageBox.open();
			}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("No valid filename created.");
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

	private void processThroughModal(File file, RiskSourceProperties props) {
		try {
			boolean isOld = file.exists();
			Runnable theModal = null;
			String chosenRootElementName = props.getRootElementName();
			if (chosenRootElementName == null) {
					theModal = new RelRiskFromOtherDiseaseModal(shell, file
							.getAbsolutePath(), file.getAbsolutePath(), rootElementName,
							node, props);
			} else {
				fillRootElementName(chosenRootElementName);
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL
						.getNodeLabel().equals(chosenRootElementName)) {
					theModal = new RelRiskFromRiskFactorCategoricalModal(
							shell, file.getAbsolutePath(), file.getAbsolutePath(),
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
									shell, file.getAbsolutePath(), file.getAbsolutePath(),
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
