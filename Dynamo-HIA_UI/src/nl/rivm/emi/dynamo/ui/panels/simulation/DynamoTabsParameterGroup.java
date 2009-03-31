package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class DynamoTabsParameterGroup {

	public Group group;

	public DynamoTabsParameterGroup(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);

		Composite dynamoTabsDataPanel = new DynamoTabsDataPanel(group,
				selectedNode, dynamoSimulationObject, dataBindingContext,
				helpGroup);
		FormData dynamoTabsFormData = new FormData();
		dynamoTabsFormData.top = new FormAttachment(100, -22);
		dynamoTabsFormData.right = new FormAttachment(100, -5);
		dynamoTabsFormData.left = new FormAttachment(0, 5);
		dynamoTabsFormData.bottom = new FormAttachment(100, -2);
		dynamoTabsDataPanel.setLayoutData(dynamoTabsFormData);		
	}

	/**
	 * 
	 * Place the last (and second) group in the container
	 * 
	 * @param topNeighbour
	 */
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

}
