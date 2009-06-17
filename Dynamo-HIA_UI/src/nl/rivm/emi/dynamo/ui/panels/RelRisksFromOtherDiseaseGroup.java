package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RelRisksFromOtherDiseaseGroup {
	Group theGroup;

	public RelRisksFromOtherDiseaseGroup(Shell shell, TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			BaseNode riskSourceNode, HelpGroup helpGroup) throws ConfigurationException, DynamoInconsistentDataException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		RelativeRiskContextPanel entityNameGroup = new RelativeRiskContextPanel(theGroup, riskSourceNode,
				selectedNode);
		entityNameGroup.handlePlacementInContainer();
		RelRiskFromOtherDiseaseParameterGroup parameterGroup = new RelRiskFromOtherDiseaseParameterGroup(
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
