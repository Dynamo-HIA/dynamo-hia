package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorCategoricalGroup {
	Group theGroup;

	public RiskFactorCategoricalGroup(Shell shell, RiskFactorCategoricalObject lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		String[] entityArray = Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
		EntityNamePanel entityNameGroup = new EntityNamePanel(theGroup,
				entityArray[0], entityArray[1]);
		entityNameGroup.putInContainer();
		RiskFactorCategoricalParameterGroup parameterGroup = new RiskFactorCategoricalParameterGroup(
				theGroup, lotsOfData, dataBindingContext, helpGroup);
		parameterGroup.handlePlacementInContainer(entityNameGroup.group);
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
