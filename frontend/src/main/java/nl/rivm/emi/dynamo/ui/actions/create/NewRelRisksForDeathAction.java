package nl.rivm.emi.dynamo.ui.actions.create;

import java.io.File;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.ui.actions.ActionBase;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathContinuousModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
//ND: Use DisplayRealm instead of SWTObservables
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewRelRisksForDeathAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public NewRelRisksForDeathAction(Shell shell, TreeViewer v,
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
						+ "relriskfordeath.xml";
				File file = new File(candidatePath);
				if (file != null && !file.getName().isEmpty()) {
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
		} catch (TreeStructureException tse) {			
			ErrorMessageUtil.showErrorMessage(this.log, this.shell, tse, 
					"Could not extract rootelementname.", SWT.ERROR_UNSUPPORTED_DEPTH);			
		} catch (DynamoConfigurationException dce) {
			ErrorMessageUtil.showErrorMessage(this.log, this.shell, dce, 
					"Could not extract rootelementname.", SWT.ERROR_UNSPECIFIED);
		}
	}

	// Add import file action here
	private void processThroughModal(File file, String configurationRootElementName) {
		try {
			FileCreationFlag.isOld = file.exists();
			Runnable theModal = null;
			if (configurationRootElementName == null) {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_NULL_ARGUMENT);
				messageBox.setMessage("No rootelementname selected.");
				messageBox.open();
			} else {
				if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
						.equals(configurationRootElementName)) {
					theModal = new RelRiskForDeathCategoricalModal(
							shell, file.getAbsolutePath(),
							file.getAbsolutePath(),
							RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
									.getNodeLabel(), node);
				} else {
					if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
							.getNodeLabel().equals(configurationRootElementName)) {
						theModal = new RelRiskForDeathContinuousModal(
								shell, file.getAbsolutePath(),
								file.getAbsolutePath(),
								RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
										.getNodeLabel(), node);
					} else {
						if (RootElementNamesEnum.RISKFACTOR_COMPOUND
								.getNodeLabel().equals(configurationRootElementName)) {
							theModal = new RelRiskForDeathCompoundModal(
									shell, file.getAbsolutePath(),
									RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND
											.getNodeLabel(), RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND
											.getNodeLabel(), node);
									} else {
							MessageBox messageBox = new MessageBox(shell,
									SWT.ERROR_UNSUPPORTED_FORMAT);
							messageBox.setMessage("\"" + configurationRootElementName
									+ "\" not supported.");
							messageBox.open();
						}
					}
				}
				if(theModal != null){				
				Realm.runWithDefault(DisplayRealm.getRealm(Display
						.getDefault()), theModal);
				boolean isPresentAfter = file.exists();
				if (isPresentAfter && !FileCreationFlag.isOld) {
					((ParentNode) node).addChild((ChildNode) new FileNode(
							(ParentNode) node, file));
					FileCreationFlag.isOld = true;
				}
				theViewer.refresh();
				}				
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
