package nl.rivm.emi.dynamo.ui.actions.create;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.ui.actions.ActionBase;
import nl.rivm.emi.dynamo.ui.main.NewbornsModal;
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

public class NewbornsXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;

	public NewbornsXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		String filePath = "";
		if (node instanceof DirectoryNode) {
			filePath = node.getPhysicalStorage().getAbsolutePath()
					+ File.separator + "newborns" + ".xml";
		} else {
			filePath = node.getPhysicalStorage().getAbsolutePath();
		}
		File file = new File(filePath);
		processThroughModal(file);
	}

	private void processThroughModal(File file) {
		try {
			boolean isOld = file.exists();
			NewbornsModal popSizeModal = new NewbornsModal(shell,
					file.getAbsolutePath(), file.getAbsolutePath(), 
					rootElementName, node, null);
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					popSizeModal);
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
			messageBox.setMessage("Processing of \"" + file.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}
}
