package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCategoricalModal;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewRelRisksForDisabilityAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public NewRelRisksForDisabilityAction(Shell shell, TreeViewer v,
			BaseNode selectedNode) {
		super(shell, v, selectedNode, "aBSTRACT");
	}

	@Override
	public void run() {
		try {
			if (node instanceof DirectoryNode) {
				String selectionPath = node.getPhysicalStorage()
						.getAbsolutePath();
				String candidatePath = selectionPath + File.separator
						+ "relriskfordisability.xml";
				File file = new File(candidatePath);
				if (file != null) {
					if (file.exists()) {
						MessageBox alreadyExistsMessageBox = new MessageBox(
								shell, SWT.ERROR_ITEM_NOT_ADDED);
						alreadyExistsMessageBox.setMessage("\"" + candidatePath
								+ "\"\n exists already.");
						alreadyExistsMessageBox.open();
					} else {
						String configurationRootElementName = ConfigurationFileUtil
								.extractRootElementNameFromChildConfiguration(node);
						processThroughModal(file, configurationRootElementName);
					}
				}
			} else {
				MessageBox messageBox = new MessageBox(shell);
				messageBox.setMessage("\"" + this.getClass().getName()
						+ "\n\" should not be called on " + "\""
						+ node.getPhysicalStorage().getName() + "\"");
				messageBox.open();
			}
		} catch (TreeStructureException e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_UNSUPPORTED_DEPTH);
			messageBox.setMessage("Could not extract rootelementname.");
			messageBox.open();
		}
	}

	private void processThroughModal(File file, String configurationRootElementName) {
		try {
			boolean isOld = file.exists();
			Runnable theModal = null;
			if (configurationRootElementName == null) {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_NULL_ARGUMENT);
				messageBox.setMessage("No rootelementname selected.");
				messageBox.open();
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(configurationRootElementName)) {
					theModal = new RelRiskForDisabilityCategoricalModal(
							shell,
							file.getAbsolutePath(),
							RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
									.getNodeLabel(), node);
				} else {
					if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
							.getNodeLabel().equals(configurationRootElementName)) {
						MessageBox messageBox = new MessageBox(shell,
								SWT.ERROR_NOT_IMPLEMENTED);
						messageBox.setMessage("\"" + configurationRootElementName
								+ "\" not yet implemented.");
						messageBox.open();
					} else {
						if (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(configurationRootElementName)) {
							MessageBox messageBox = new MessageBox(shell,
									SWT.ERROR_NOT_IMPLEMENTED);
							messageBox.setMessage("\"" + configurationRootElementName
									+ "\" not yet implemented.");
							messageBox.open();
						} else {
							MessageBox messageBox = new MessageBox(shell,
									SWT.ERROR_UNSUPPORTED_FORMAT);
							messageBox.setMessage("\"" + configurationRootElementName
									+ "\" not supported.");
							messageBox.open();
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
