package nl.rivm.emi.dynamo.ui.main.parameters;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.parameters.BaselineOtherMortalitiesGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author boshuizh
 *
 */
public class BaselineAbilityModal extends AbstractDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public BaselineAbilityModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Baseline ability";
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
			BaselineOtherMortalitiesGroup abilityGroup = new BaselineOtherMortalitiesGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			abilityGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(500, ModalStatics.defaultHeight);
			this.shell.open();
	}

}