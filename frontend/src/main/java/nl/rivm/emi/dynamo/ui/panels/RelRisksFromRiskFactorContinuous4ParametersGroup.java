package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
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

public class RelRisksFromRiskFactorContinuous4ParametersGroup {
	Group theGroup;

	public RelRisksFromRiskFactorContinuous4ParametersGroup(Shell shell,
			TypedHashMap modelObject, DataBindingContext dataBindingContext,
			BaseNode selectedNode, BaseNode riskSourceNode, HelpGroup helpGroup) throws ConfigurationException, DynamoInconsistentDataException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		
		ParametersFileContextPanel relRiskPanel = new ParametersFileContextPanel(theGroup,
				riskSourceNode,selectedNode);
		relRiskPanel.handlePlacementInContainer();
		RelRisksFromRiskFactorContinuousParameterGroup parameterGroup = new RelRisksFromRiskFactorContinuousParameterGroup(
				theGroup, modelObject, dataBindingContext, helpGroup);
		parameterGroup.handlePlacementInContainer(relRiskPanel.group);
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
