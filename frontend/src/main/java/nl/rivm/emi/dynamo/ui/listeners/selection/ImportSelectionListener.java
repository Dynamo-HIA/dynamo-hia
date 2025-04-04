package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.dynamo.global.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.actions.XMLFileAction;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;
import nl.rivm.emi.dynamo.ui.util.CategoricalRiskFactorProperties;
import nl.rivm.emi.dynamo.ui.util.CompoundRiskFactorProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

public class ImportSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	DataAndFileContainer modalParent;

	public ImportSelectionListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Handles the import file event
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent arg0) {
		this.log.info("Control "
				+ ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");

		// Retrieve the import file path
		String dataPath = this.getImportFile().getAbsolutePath();

		File dataFile = new File(dataPath);

		// Check if a file has been selected
		if (dataFile.isFile() && dataFile.canRead()) {
			// Destroy the existing data screen with 'old' data
			this.modalParent.getShell().dispose();
			String rootElementName = (String) this.modalParent
					.getRootElementName();
			XMLFileAction action = new XMLFileAction(this.modalParent
					.getParentShell(), TreeViewerPlusCustomMenu
					.getTreeViewerInstance(), this.modalParent.getBaseNode(),
					rootElementName, rootElementName);

			/* Workaround for import for an unsaved configuration file. */
			if (this.modalParent instanceof RelRiskFromRiskFactorCategoricalModal) {
				CategoricalRiskFactorProperties props = ((RelRiskFromRiskFactorCategoricalModal) this.modalParent)
						.getProps();
				action.setProps(props);
			} else {
				if (this.modalParent instanceof RelRiskFromRiskFactorCompoundModal) {
					CompoundRiskFactorProperties props = ((RelRiskFromRiskFactorCompoundModal) this.modalParent)
							.getProps();
					action.setProps(props);
				}

			}
			// Assume a different file has been imported.
			this.modalParent.setChanged(true);
			action.processThroughModal(dataFile, new File(this.modalParent
					.getConfigurationFilePath()));
		}
	}

	/**
	 * @return File The selected import file
	 */
	public File getImportFile() {
		FileDialog fileDialog = new FileDialog(this.modalParent.getShell());
		fileDialog.open();
		return new File(fileDialog.getFilterPath() + File.separator
				+ fileDialog.getFileName());
	}

}
