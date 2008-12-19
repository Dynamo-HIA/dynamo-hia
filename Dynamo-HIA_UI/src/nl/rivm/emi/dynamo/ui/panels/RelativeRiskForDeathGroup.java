package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RelativeRiskForDeathGroup {
	Group theGroup;

	public RelativeRiskForDeathGroup(Shell shell, TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		EntityNamePanel entityNameGroup = new EntityNamePanel(theGroup,
				selectedNode, helpGroup);
		entityNameGroup.handlePlacementInContainer();
		RelativeRiskForDeathParameterGroup parameterGroup = new RelativeRiskForDeathParameterGroup(
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
