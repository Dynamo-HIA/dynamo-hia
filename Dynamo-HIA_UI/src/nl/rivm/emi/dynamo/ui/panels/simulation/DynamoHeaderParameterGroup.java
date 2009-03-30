package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.ReferenceClassDataPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class DynamoHeaderParameterGroup {

	public Group group;

	public DynamoHeaderParameterGroup(Composite parent,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		// TODO Auto-generated constructor stub

		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);

		Composite dynamoHeaderDataPanel = new DynamoHeaderDataPanel(group,
				null, dynamoSimulationObject, dataBindingContext, selectedNode,
				helpGroup);
		FormData dynamoHeaderFormData = new FormData();
		dynamoHeaderFormData.top = new FormAttachment(100, -22);
		dynamoHeaderFormData.right = new FormAttachment(100, -5);
		dynamoHeaderFormData.left = new FormAttachment(0, 5);
		dynamoHeaderFormData.bottom = new FormAttachment(100, -2);
		dynamoHeaderDataPanel.setLayoutData(dynamoHeaderFormData);

		// TODO: CONTINUE HERE ON MONDAY
		// ClassDefinitionsDataPanel
		// ReferenceClassDataPanel

	}

	public void putFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);
	}

}
