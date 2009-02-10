package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksContinuousGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class RelRiskForDisabilityContinuousModal extends AgnosticModal implements
		Runnable, DataAndFileContainer {

	public RelRiskForDisabilityContinuousModal(Shell parentShell,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, configurationFilePath, rootElementName, selectedNode);
	}

	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risk for disability, continuous,";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel) {
		RelativeRisksContinuousGroup relativeRiskForDisabilityGroup = new RelativeRisksContinuousGroup(
				shell, modelObject, dataBindingContext, selectedNode, helpPanel);
		relativeRiskForDisabilityGroup
				.setFormData(helpPanel.getGroup(), buttonPanel);
	}
}
