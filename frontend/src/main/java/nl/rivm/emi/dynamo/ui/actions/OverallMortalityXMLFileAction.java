package nl.rivm.emi.dynamo.ui.actions;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.ui.main.OverallMortalityModal;
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

public class OverallMortalityXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;

	public OverallMortalityXMLFileAction(Shell shell, TreeViewer v,
			BaseNode node, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		String filePath = "";
		if (node instanceof DirectoryNode) {
			filePath = node.getPhysicalStorage().getAbsolutePath()
					+ File.separator + "overallmortality" + ".xml";
		} else {
			filePath = node.getPhysicalStorage().getAbsolutePath();
		}
		File file = new File(filePath);
		processThroughModal(file);
	}

	private void processThroughModal(File file) {
		try {
			FileCreationFlag.isOld = file.exists();
			OverallMortalityModal popSizeModal = new OverallMortalityModal(shell,
					file.getAbsolutePath(), file.getAbsolutePath(), rootElementName, node);
			Realm.runWithDefault(DisplayRealm.getRealm(Display.getDefault()),
					popSizeModal);
			boolean isPresentAfter = file.exists();
			if (isPresentAfter && !FileCreationFlag.isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, file));
				FileCreationFlag.isOld = true;
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
