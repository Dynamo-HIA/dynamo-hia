package nl.rivm.emi.dynamo.ui.panels.parameters;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.ParametersFileContextPanel;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RelativeRisksCategoricalGroup {
	Group theGroup;

	public RelativeRisksCategoricalGroup(Shell shell,
			@SuppressWarnings("rawtypes") TypedHashMap modelObject, DataBindingContext dataBindingContext,
			BaseNode selectedNode, HelpGroup helpGroup) throws ConfigurationException, DynamoInconsistentDataException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		ParametersFileContextPanel entityNamePanel = new ParametersFileContextPanel(theGroup, null, selectedNode);
		entityNamePanel.handlePlacementInContainer();
		RelativeRisksCategoricalParameterGroup parameterGroup = new RelativeRisksCategoricalParameterGroup(
				theGroup, modelObject, dataBindingContext, helpGroup);
		parameterGroup.handlePlacementInContainer(null);
	}

	public void setFormData(Composite rightNeighbour, Composite lowerNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(rightNeighbour, -2);
		formData.bottom = new FormAttachment(lowerNeighbour, -5);
		theGroup.setLayoutData(formData);
	}
}
