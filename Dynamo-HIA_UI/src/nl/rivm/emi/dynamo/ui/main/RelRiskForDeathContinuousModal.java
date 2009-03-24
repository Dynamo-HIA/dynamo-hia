package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.ui.panels.RelRisksForDeathContinuousGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RelRiskForDeathContinuousModal extends AgnosticModal {

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
	public RelRiskForDeathContinuousModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risk for death, continuous,";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel) throws ConfigurationException {
		RelRisksForDeathContinuousGroup relativeRiskForDeathGroup = new RelRisksForDeathContinuousGroup(
				this.shell, this.lotsOfData, this.dataBindingContext,
				this.selectedNode, this.helpPanel);
		relativeRiskForDeathGroup.setFormData(this.helpPanel.getGroup(),
				buttonPanel);
	}
}