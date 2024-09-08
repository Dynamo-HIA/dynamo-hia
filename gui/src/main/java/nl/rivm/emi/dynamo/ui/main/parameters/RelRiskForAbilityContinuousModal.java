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
import nl.rivm.emi.dynamo.ui.main.base.AgnosticModal;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksContinuousGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RelRiskForAbilityContinuousModal extends AgnosticModal {

	/**
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public RelRiskForAbilityContinuousModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risk for ability, continuous,";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel)
			throws ConfigurationException {
		RelativeRisksContinuousGroup relativeRiskForAbilityGroup = new RelativeRisksContinuousGroup(
				this.shell, this.modelObject, this.dataBindingContext,
				this.selectedNode, this.helpPanel);
		relativeRiskForAbilityGroup.setFormData(this.helpPanel.getGroup(),
				buttonPanel);
	}

	/**
	 * The AgnosticModal layer handles the opening.... Must be present to keep
	 * the compiler happy.
	 */
	@Override
	public void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
	}
}
