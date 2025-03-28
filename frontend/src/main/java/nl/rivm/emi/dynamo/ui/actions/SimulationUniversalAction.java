package nl.rivm.emi.dynamo.ui.actions;

/**
 * Action that must be able to handle three uses-cases:
 * 1. Create a new simulation from scratch from the "Simulations"-node.
 * 2. Open a simulation-configuration from the "<simulation-name"> node.
 * 3. Open a simulation-configuration from its own node.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.global.StorageTreeException;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.validators.FileAndDirectoryNameInputValidator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
//ND: Use DisplayRealm instead of SWTObservables
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
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
	private Boolean configurationFileExistsBefore = null;
	/**
	 * Flag indicating whether the preexisting configuration file is readonly.
	 */
	private Boolean configurationFileExistsAndReadOnly = false;
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
				configurationFileExistsBefore = false;
			} else {
				BaseNode parentNode = (BaseNode) ((ChildNode) node).getParent();
				if (StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
						.equals(parentNode.deriveNodeLabel())) {
					simulationsDirectoryNode = (DirectoryNode) parentNode;
					simulationNameDirectoryNode = (DirectoryNode) node;
					detectAndCategorizePreExistingConfigFile();
				} else {
					menuLabel = "This is not a supported DirectoryNode";
				}
			}
		} else {
			// Not a DirectoryNode.
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
						if (!configurationFile.canWrite()) {
							configurationFileExistsAndReadOnly = true;
						}
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
					if(!configurationFileExistsAndReadOnly){
					menuLabel = "Configure and run simulation";
					} else {
						menuLabel = "View and run simulation";
					}
				}
			} else {
				menuLabel = "\"Simulations\" directory should be directly below the base-directory.";
				// Invalidate the action state.
				simulationsDirectoryNode = null;
			}
		}
	}

	private void detectAndCategorizePreExistingConfigFile() {
		Object[] children = ((ParentNode) node).getChildren();
		configurationFileExistsBefore = false;
		String testLabel = StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
				.getNodeLabel();
		for (Object child : children) {
			if (child instanceof FileNode) {
				String childLabel = ((BaseNode) child).deriveNodeLabel();
				if (testLabel.equalsIgnoreCase(childLabel)) {
					configurationFileExistsBefore = true;
					File configurationFile = ((FileNode) child)
							.getPhysicalStorage();
					if (!configurationFile.canWrite()) {
						configurationFileExistsAndReadOnly = true;
					}
				}
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
				// RLM Testing is the only way to make sure.
				if (simulationNameDirectoryNode != null) {
					if (simulationConfigurationFileNode == null) {
						createSimulationConfigurationFileObject();
					}
					editConfigurationFile();
				}
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
		DirectoryNode newSimulationDirectoryNode = null;
		String simulationsPath = ((BaseNode) simulationsDirectoryNode)
				.getPhysicalStorage().getAbsolutePath();
		InputDialog inputDialog = new InputDialog(shell, "SimulationsPath: "
				+ simulationsPath, "Enter name for new simulation",
				"SimulationName", new FileAndDirectoryNameInputValidator());
		inputDialog.open();
		int returnCode = inputDialog.getReturnCode();
		if(returnCode == Window.OK){
		newSimulationDirectoryNode =  handleOKedSimulationName(simulationsNode,
				newSimulationDirectoryNode, simulationsPath, inputDialog);
	}
		return newSimulationDirectoryNode;
	}

	private DirectoryNode handleOKedSimulationName(
			DirectoryNode simulationsNode,
			DirectoryNode newSimulationDirectoryNode, String simulationsPath,
			InputDialog inputDialog) throws StorageTreeException {
		String candidateSimulationName = inputDialog.getValue();
		String candidateSimulationPath = simulationsPath + File.separator
				+ candidateSimulationName;
		File candidateDirectory = new File(candidateSimulationPath);
		if (!candidateDirectory.exists()) {
			// Create it physically.
			if (candidateDirectory.mkdir()) {
				newSimulationDirectoryNode = new DirectoryNode(simulationsNode,
						candidateDirectory);
				simulationsNode.addChild(newSimulationDirectoryNode);
				theViewer.refresh();
			} else {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_ITEM_NOT_ADDED);
				messageBox.setText("Creation error.");
				messageBox
						.setMessage("The simulation directory: \""
								+ candidateSimulationName
								+ "\"\ncould not be created.");
				messageBox.open();
			}
		} else {
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setText("Creation error.");
			messageBox.setMessage("The simulation directory: \""
					+ candidateSimulationName + "\"\nalready exists.");
			messageBox.open();
		}
		return newSimulationDirectoryNode;
	}

	/**
	 * Creates the File Object in memory, but does not persist it.
	 */
	private void createSimulationConfigurationFileObject() {
		String configurationFilePath = simulationNameDirectoryNode
				.getPhysicalStorage().getAbsolutePath()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.CONFIGURATIONFILE.getNodeLabel()
				+ ".xml";
		configurationFile = new File(configurationFilePath);
	}

	public void editConfigurationFile() {
		try {
			if (simulationPreConditionsMet()) {
				Boolean bogusFlag = true; // Shut up any "intelligence" inside
				// the modal.
				SimulationModal theModal = new SimulationModal(shell,
						configurationFile.getAbsolutePath(), configurationFile
								.getAbsolutePath(),
						RootElementNamesEnum.SIMULATION.getNodeLabel(),
						simulationNameDirectoryNode,
						/* configurationFileExistsBefore */bogusFlag);
				if (theModal != null) {
					Realm.runWithDefault(DisplayRealm.getRealm(Display
							.getDefault()), theModal);
					boolean isPresentAfter = configurationFile.exists();
					if (isPresentAfter
							&& (configurationFileExistsBefore != null)
							&& !configurationFileExistsBefore) {
						((ParentNode) simulationNameDirectoryNode)
								.addChild((ChildNode) new FileNode(
										(ParentNode) simulationNameDirectoryNode,
										configurationFile));
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

	/** configured
	 * Method for checking the preconditions for making a new simulation
	 * configuration. When this check fails a messagebox indicating the error(s)
	 * is shown and false is returned.
	 * from Dynamo 2.07 on it does not check any more for diseases
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
		int numberOfPopulations = 0;
		try{
		 numberOfPopulations = instance.getPopulations().size();}
		catch (NullPointerException e) {
			numberOfPopulations=0;
		}
		if (numberOfPopulations == 0) {
			allTestsOK = false;
			errorMessage.append("No valid population was found.\n");
		}
		int numberOfDiseases = 0;
			try{	
				numberOfDiseases =instance.getValidDiseaseNames().size();
				}
			catch (NullPointerException e) {
				numberOfDiseases=0;
			}
		//if (numberOfDiseases == 0) {
		//	allTestsOK = false;
		//	errorMessage.append("No valid disease was found.\n");
		//}
		int numberOfRiskFactors=0;
		try{
		 numberOfRiskFactors = instance.getRiskFactorNames().size();}
		catch (NullPointerException e) {
			numberOfRiskFactors=0;
		}
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
