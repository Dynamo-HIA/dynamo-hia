package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorCategoricalPrevalencesGroup {
	Group theGroup;

	public RiskFactorCategoricalPrevalencesGroup(Shell shell,
			TypedHashMap modelObject, DataBindingContext dataBindingContext,
			BaseNode selectedNode, HelpGroup helpGroup) throws ConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		String[] entityStrings = Util.deriveEntityLabelAndValueFromSelectedNode(selectedNode);
		EntityNamePanel entityNamePanel = new EntityNamePanel(theGroup, entityStrings[0], entityStrings[1]);
		entityNamePanel.putInContainer();
		RiskFactorCategoricalPrevalencesParameterGroup parameterGroup = new RiskFactorCategoricalPrevalencesParameterGroup(
				theGroup, modelObject, dataBindingContext, helpGroup);
		parameterGroup.handlePlacementInContainer(entityNamePanel.group);
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