package nl.rivm.emi.dynamo.ui.actions;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DeleteXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	private String fileNameTrunk;

	public DeleteXMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String fileNameTrunk) {
		super(shell, v, node, "IsNeverNeeded");
		this.fileNameTrunk = fileNameTrunk;
	}

	@Override
	public void run() {
		String filePath = "";
		// Do not delete directories (yet).
		if (node instanceof DirectoryNode) {
			return;
		} else {
			filePath = node.getPhysicalStorage().getAbsolutePath();
		}
		File savedFile = new File(filePath);
		MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
		messageBox.setText("Removing file.");
		messageBox
				.setMessage("The file " + fileNameTrunk + "will be deleted.\nIs that what you want?");
		int returnCode = messageBox.open();
		switch (returnCode) {
		case SWT.YES:
			log.debug("MessageBox returns: " + "SWT.YES");
			deleteNode(savedFile);
			break;
		case SWT.OK:
			log.debug("MessageBox returns: " + "SWT.OK");
			deleteNode(savedFile);
			break;
		case SWT.CANCEL:
			log.debug("MessageBox returns: " + "SWT.CANCEL");
			break;
		case SWT.NO:
			log.debug("MessageBox returns: " + "SWT.NO");
			break;
		case SWT.RETRY:
			log.debug("MessageBox returns: " + "SWT.RETRY");
			break;
		case SWT.ABORT:
			log.debug("MessageBox returns: " + "SWT.ABORT");
			break;
		case SWT.IGNORE:
			log.debug("MessageBox returns: " + "SWT.IGNORE");
			break;
		default:
			log.debug("MessageBox returns something unexpected. Returncode: "
					+ returnCode);
		}
	}

	private void deleteNode(File savedFile) {
		MessageBox messageBox;
		if(savedFile.delete()){
			ParentNode parentNode = ((ChildNode)node).getParent();
			parentNode.removeChild((ChildNode)node);
			theViewer.refresh();
		} else {
			messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
			messageBox.open();
		}
	}
}
