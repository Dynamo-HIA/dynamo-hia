package nl.rivm.emi.dynamo.ui.treecontrol.menu;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.global.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class StorageTreeMenuListener implements IMenuListener {

	private Log log = LogFactory.getLog(this.getClass().getName());

	Shell shell;
	TreeViewer treeViewer;
	StorageTreeMenuFactory stmf;

	public StorageTreeMenuListener(Shell shell, TreeViewer treeViewer) {
		this.shell = shell;
		this.treeViewer = treeViewer;
		stmf = new StorageTreeMenuFactory(shell, treeViewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface
	 * .action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager manager) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (!selection.isEmpty()) {
			BaseNode selectedNode = (BaseNode) selection.getFirstElement();
			try {
				stmf
						.createRelevantContextMenu(manager, selection,
								selectedNode);
			} catch (DynamoConfigurationException dce) {
				showErrorMessage(dce);
			} catch (TreeStructureException tse) {
				showErrorMessage(tse);
			} catch (ConfigurationException ce) {
				showErrorMessage(ce);
			}
		}
	}

	private void showErrorMessage(Exception e) {
		this.log.fatal(e);
//		log.warn(e.getStackTrace());
		MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during save " + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

}