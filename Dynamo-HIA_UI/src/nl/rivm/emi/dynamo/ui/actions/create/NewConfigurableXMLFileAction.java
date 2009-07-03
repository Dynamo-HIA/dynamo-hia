package nl.rivm.emi.dynamo.ui.actions.create;

/**
 * DEvelop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class NewConfigurableXMLFileAction extends NewActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;
	private String fileName;

	public NewConfigurableXMLFileAction(Shell shell, TreeViewer v,
			DirectoryNode node, String rootElementName, String fileName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			if (fileName == null) {
				InputDialog inputDialog = new InputDialog(shell,
						"Create file in the selected directory",
						"Enter name for new " + abstractName, "Name", null);
				inputDialog.open();
				int returnCode = inputDialog.getReturnCode();
				log.fatal("ReturnCode is: " + returnCode);
				if (returnCode != Window.CANCEL) {
					String candidateName = inputDialog.getValue();
					String candidatePath = node.getPhysicalStorage()
							.getAbsolutePath()
							+ File.separator + candidateName;
					handleCreation(candidateName, candidatePath);
				}
				// else {
				// MessageBox messageBox = new MessageBox(shell);
				// messageBox.setMessage("New cancelled.");
				// messageBox.open();
				// }
			} else {
				String candidatePath = node.getPhysicalStorage()
						.getAbsolutePath()
						+ File.separator + fileName;
				handleCreation(fileName, candidatePath);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected void handleCreation(String candidateName, String candidatePath)
			throws StorageTreeException {
		if (RootElementNamesEnum.POPULATIONSIZE.getNodeLabel().equals(
				rootElementName)) {
			PopulationSizeModal popSizeModal = new PopulationSizeModal(shell,
					candidatePath + ".xml", candidatePath + ".xml", this.rootElementName, null);
			popSizeModal.open();
		} else {
			log.fatal("What else?");
		}

		// File candidateFile = new File(candidatePath + ".xml");
		// if (!candidateFile.exists()/* && candidateFile.createNewFile() */) {
		// AgnosticFactory factory = FactoryProvider
		// .getRelevantFactoryByRootNodeName(rootElementName);
		// TypedHashMap modelObject = factory.manufactureDefault();
		// MessageBox messageBox = new MessageBox(shell);
		// messageBox.setMessage("\"" + candidateName
		// + "\"\nhas been created.");
		// messageBox.open();
		// node.addChild(new DirectoryNode(node, candidateFile));
		// theViewer.refresh();
		// } else {
		// MessageBox messageBox = new MessageBox(shell,
		// SWT.ERROR_ITEM_NOT_ADDED);
		// messageBox.setMessage("\"" + candidateName
		// + "\"\ncould not be created.");
		// messageBox.open();
		// }
		// } catch (Exception e) {
		// throw new StorageTreeException(
		// e.getClass().getName() + " thrown during creation of file "
		// + candidatePath + " with message: "
		// + e.getMessage());
		// }
	}

}
