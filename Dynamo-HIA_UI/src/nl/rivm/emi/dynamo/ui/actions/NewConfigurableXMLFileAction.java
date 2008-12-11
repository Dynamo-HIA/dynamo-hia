package nl.rivm.emi.dynamo.ui.actions;

/**
 * DEvelop with populationSize as concrete implementation.
 */
import java.io.File;
import java.io.IOException;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewConfigurableXMLFileAction extends NewActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;

	public NewConfigurableXMLFileAction(Shell shell, TreeViewer v, DirectoryNode node,
			String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	protected void handleCreation(String candidateName, String candidatePath)
			throws StorageTreeException {
		// try {
		PopulationSizeModal popSizeModal = new PopulationSizeModal(shell,
				candidatePath + ".xml", rootElementName, null);
		popSizeModal.open();
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
