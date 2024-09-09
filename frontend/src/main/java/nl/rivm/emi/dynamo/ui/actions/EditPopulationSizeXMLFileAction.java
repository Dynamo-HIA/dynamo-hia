package nl.rivm.emi.dynamo.ui.actions;

/**
 * DEvelop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
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

public class EditPopulationSizeXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String rootElementName;

	public EditPopulationSizeXMLFileAction(Shell shell, TreeViewer v,
			DirectoryNode node, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		handle("size", node.getPhysicalStorage().getAbsolutePath()
				+ File.separator + "size");
	}

	protected void handle(String candidateName, String candidatePath) {
		File candidateFile = new File(candidatePath + ".xml");
		try {
			FileCreationFlag.isOld = candidateFile.exists();
			PopulationSizeModal popSizeModal = new PopulationSizeModal(shell,
					candidatePath + ".xml", candidatePath + ".xml", rootElementName, node);
			Realm.runWithDefault(DisplayRealm.getRealm(Display.getDefault()),
					popSizeModal);
			boolean isPresentAfter = candidateFile.exists();
			if (isPresentAfter && !FileCreationFlag.isOld) {
				((ParentNode)node).addChild(new FileNode((ParentNode)node, candidateFile));
				FileCreationFlag.isOld = true;
			}
			theViewer.refresh();
		} catch (Exception e) {
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + candidateName
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}
}
