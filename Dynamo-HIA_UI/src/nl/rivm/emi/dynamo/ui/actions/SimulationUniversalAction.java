package nl.rivm.emi.dynamo.ui.actions;

/**
 * Action that must be able to handle three uses-cases:
 * 1. Create a new simulation from scratch from the "Simulations"-node.
 * 2. Open a simulation-configuration from the "<simulation-name"> node.
 * 3. Open a simulation-configuration from its own node.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SimulationUniversalAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Reference to the tree-node as part of the action state and for use during
	 * the action.
	 */
	private DirectoryNode simulationsDirectoryNode = null;
	/**
	 * Reference to the tree-node as part of the action state and for use during
	 * the action.
	 */
	private DirectoryNode simulationNameDirectoryNode = null;
	/**
	 * Reference to the tree-node as part of the action state and for use during
	 * the action.
	 */
	private FileNode simulationConfigurationFileNode = null;
	/**
	 * In memory instance of the configuration file.
	 */
	private File configurationFile = null;
	/**
	 * Flag indicating whether the configuration file existed on disk when the
	 * action was initialized.
	 */
	private boolean configurationFileExistsBefore = false;
	/**
	 * Label to put in the context-menu that triggers this action. This label
	 * can contain feedback.
	 */
	private String menuLabel = "uninitialized";

	public SimulationUniversalAction(Shell shell, TreeViewer v, BaseNode node,
			String abstractName) {
		super(shell, v, node, abstractName);
		initialize();
	}

	public String getMenuLabel() {
		return menuLabel;
	}

	private void initialize() {
		if (node instanceof DirectoryNode) {
			if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel().equals(
					node.deriveNodeLabel())) {
				simulationsDirectoryNode = (DirectoryNode) node;
			} else {
				BaseNode parentNode = (BaseNode) ((ChildNode) node).getParent();
				if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
						.equals(parentNode.deriveNodeLabel())) {
					simulationsDirectoryNode = (DirectoryNode) parentNode;
					simulationNameDirectoryNode = (DirectoryNode) node;
				} else {
					menuLabel = "This is not a supported DirectoryNode";
				}
			}
			// Not a DirectoryNode.
		} else {
			if (node instanceof FileNode) {
				if ((StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
						.getNodeLabel()).equalsIgnoreCase(node
						.deriveNodeLabel())) {
					BaseNode parentNode = (BaseNode) ((ChildNode) node)
							.getParent();
					BaseNode grandParentNode = (BaseNode) ((ChildNode) parentNode)
							.getParent();
					if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
							.equals(grandParentNode.deriveNodeLabel())) {
						simulationsDirectoryNode = (DirectoryNode) grandParentNode;
						simulationNameDirectoryNode = (DirectoryNode) parentNode;
						simulationConfigurationFileNode = (FileNode) node;
						configurationFile = ((FileNode) node)
								.getPhysicalStorage();
						configurationFileExistsBefore = true;
					} else {
						menuLabel = "The selected fileNode is not two levels below the \"Simulations\" node.";
					}
				} else {
					menuLabel = "The FileNode should have \"configuration\" as label.";
				}
			} else {
				menuLabel = "The type of the Node is unsupported";
			}
		}
		// So far, so good. One final check.
		if ("uninitialized".equals(menuLabel)) {
			ParentNode testNode = simulationsDirectoryNode.getParent();
			if (testNode instanceof BaseNode) {
				if (simulationNameDirectoryNode == null) {
					menuLabel = "Create, configure and run simulation";
				} else {
					menuLabel = "Configure and run simulation";
				}
			} else {
				menuLabel = "\"Simulations\" directory should be directly below the base-directory.";
				// Invalidate the action state.
				simulationsDirectoryNode = null;
			}
		}
	}

	@Override
	public void run() {
		try {
			if (simulationsDirectoryNode != null) {
				if (simulationNameDirectoryNode == null) {
					simulationNameDirectoryNode = createNewSimulationDirectory((DirectoryNode) node);
				}
				// simulationNameDirectoryNode should be != null here.
				if (simulationConfigurationFileNode == null) {
					configurationFileExistsBefore = createSimulationConfigurationFile();
				}
				editConfigurationFile();
			} else {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_CANNOT_SET_MENU);
				messageBox.setText("Software error!");
				messageBox.setMessage("This menu entry has been put"
						+ "\nat the wrong place in the tree.");
			}
		} catch (StorageTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private DirectoryNode createNewSimulationDirectory(
			DirectoryNode simulationsNode) throws StorageTreeException {
		String simulationsPath = ((BaseNode) simulationsDirectoryNode)
				.getPhysicalStorage().getAbsolutePath();
		InputDialog inputDialog = new InputDialog(shell, "SimulationsPath: "
				+ simulationsPath, "Enter name for new simulation",
				"Simulation name.", new FileAndDirectoryNameInputValidator());
		inputDialog.open();
		String candidateSimulationName = inputDialog.getValue();
		String candidateSimulationPath = simulationsPath + File.separator
				+ candidateSimulationName;
		File candidateDirectory = new File(candidateSimulationPath);
		if (!candidateDirectory.exists() && candidateDirectory.mkdir()) {
			MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.CHECK);
			messageBox.setText("Success");
			messageBox.setMessage(candidateSimulationName
					+ "\nhas been created.");
//			messageBox.open();
			DirectoryNode newSimulationDirectoryNode = new DirectoryNode(
					simulationsNode, candidateDirectory);
			simulationsNode.addChild(newSimulationDirectoryNode);
			theViewer.refresh();
			return newSimulationDirectoryNode;
		} else {
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_CANNOT_SET_MENU);
			messageBox.setText("Software error!");
			messageBox.setMessage(candidateSimulationName
					+ "\ncould not be created.");
			messageBox.open();
			return null;
		}
	}

	/**
	 * Creates the File Object in memory, but does not persist it.
	 * 
	 * @return The File object.
	 */
	private boolean createSimulationConfigurationFile() {
		String configurationFilePath = simulationNameDirectoryNode
				.getPhysicalStorage().getAbsolutePath()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.CONFIGURATIONFILE.getNodeLabel()
				+ ".xml";
		configurationFile = new File(configurationFilePath);
		configurationFileExistsBefore = configurationFile.exists();
		return (configurationFileExistsBefore);
	}

	public void editConfigurationFile() {
		try {
			if (simulationPreConditionsMet()) {
				SimulationModal theModal = new SimulationModal(shell,
						configurationFile.getAbsolutePath(), configurationFile
								.getAbsolutePath(),
						RootElementNamesEnum.SIMULATION.getNodeLabel(),
						simulationNameDirectoryNode,
						configurationFileExistsBefore);
				if (theModal != null) {
					Realm.runWithDefault(SWTObservables.getRealm(Display
							.getDefault()), theModal);
					boolean isPresentAfter = configurationFile.exists();
					if (isPresentAfter && /*!configurationFileExistsBefore*/ !FileCreationFlag.isOld) {
						((ParentNode) simulationNameDirectoryNode)
								.addChild((ChildNode) new FileNode(
										(ParentNode) simulationNameDirectoryNode,
										configurationFile));
						FileCreationFlag.isOld = true;
						theViewer.refresh();
					}
				}
			}
		} catch (ConfigurationException e) {
			MessageBox errorMessageBox = new MessageBox(shell,
					SWT.ERROR_UNSPECIFIED);
			errorMessageBox.setText(e.getClass().getName() + " caught.");
			errorMessageBox.setMessage(e.getMessage());
			errorMessageBox.open();
			e.printStackTrace();
		} catch (StorageTreeException e) {
			MessageBox errorMessageBox = new MessageBox(shell,
					SWT.ERROR_UNSPECIFIED);
			errorMessageBox.setText(e.getClass().getName() + " caught.");
			errorMessageBox.setMessage(e.getMessage());
			errorMessageBox.open();
			e.printStackTrace();
		}
	}

	/**
	 * Method for checking the preconditions for making a new simulation
	 * configuration. When this check fails a messagebox indicating the error(s)
	 * is shown and false is returned.
	 * 
	 * @return true when creating a new simulation-configuration can go ahead.
	 *         false when no further action should be taken.
	 * @throws ConfigurationException
	 */
	private boolean simulationPreConditionsMet() throws ConfigurationException {
		boolean allTestsOK = true;
		StringBuffer errorMessage = new StringBuffer(
				"Not all preconditions have been met:\n");
		TreeAsDropdownLists instance = TreeAsDropdownLists.getInstance(node);
		instance.refresh(node);
		int numberOfPopulations = instance.getPopulations().size();
		if (numberOfPopulations == 0) {
			allTestsOK = false;
			errorMessage.append("No valid population was found.\n");
		}
		int numberOfDiseases = instance.getValidDiseaseNames().size();
		if (numberOfDiseases == 0) {
			allTestsOK = false;
			errorMessage.append("No valid disease was found.\n");
		}
		int numberOfRiskFactors = instance.getRiskFactorNames().size();
		if (numberOfRiskFactors == 0) {
			allTestsOK = false;
			errorMessage.append("No valid risk factor was found.\n");
		}
		if (!allTestsOK) {
			MessageBox errorMessageBox = new MessageBox(shell,
					SWT.ERROR_NULL_ARGUMENT);
			errorMessageBox.setMessage(errorMessage.toString());
			errorMessageBox.open();
		}
		return allTestsOK;
	}

}
