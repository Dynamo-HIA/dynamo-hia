package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksContinuousGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RelRiskForDisabilityContinuousModal extends AgnosticModal {

	/**
	 * @param parentShell
	 * @param dataFilePath 
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public RelRiskForDisabilityContinuousModal(Shell parentShell,
			String dataFilePath, String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath, rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risk for disability, continuous,";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel) throws ConfigurationException {
		RelativeRisksContinuousGroup relativeRiskForDisabilityGroup = new RelativeRisksContinuousGroup(
				this.shell, this.lotsOfData, this.dataBindingContext,
				this.selectedNode, this.helpPanel);
		relativeRiskForDisabilityGroup.setFormData(this.helpPanel.getGroup(),
				buttonPanel);
	}
}
