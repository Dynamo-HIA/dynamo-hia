package nl.rivm.emi.dynamo.ui.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Action for deserializing and displaying the resultsObject from a prior
 * simulation-run..
 */
public class ResultsObjFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	public ResultsObjFileAction(Shell shell, TreeViewer v, BaseNode node) {
		super(shell, v, node, "bogus");
	}

	@Override
	public void run() {
		String filePath = node.getPhysicalStorage().getAbsolutePath();
		File savedFile = new File(filePath);
		deSerializeAndDisplay(savedFile);
	}

	public void deSerializeAndDisplay(File savedFile) {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(savedFile)));
			DynamoOutputFactory deserializedObject = (DynamoOutputFactory) in
					.readObject();
			log.fatal(deserializedObject.getDiseaseNames());
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + savedFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}

}
