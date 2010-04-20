package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.DALYWeightsGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class DALYWeightsModal extends AbstractDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public DALYWeightsModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "DALY Weights";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = manufactureModelObject();
			DALYWeightsGroup dalyWeightsGroup = new DALYWeightsGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			dalyWeightsGroup
					.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(900, ModalStatics.defaultHeight);
			this.shell.setSize(475, ModalStatics.defaultHeight);
			this.shell.open();
	}
}
