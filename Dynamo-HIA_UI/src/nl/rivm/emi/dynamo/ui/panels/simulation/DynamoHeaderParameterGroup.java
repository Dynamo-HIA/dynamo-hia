package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * Defines the parameter fields on the simulation panel header
 * 
 * @author schutb
 *
 */
public class DynamoHeaderParameterGroup {

	public Composite group;

	public DynamoHeaderParameterGroup(Composite parent,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		group = new Composite(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);

		DynamoTabDataManager dynamoTabDataManager =
			new DynamoSimulationHeaderDataManager(selectedNode, dynamoSimulationObject);
		Composite dynamoHeaderDataPanel;
		
			dynamoHeaderDataPanel = new DynamoHeaderDataPanel(group,
					null, dynamoSimulationObject, dataBindingContext, selectedNode,
					helpGroup, dynamoTabDataManager);
		
			
			
			
			
		FormData dynamoHeaderFormData = new FormData();
		dynamoHeaderFormData.top = new FormAttachment(0, 0);
		dynamoHeaderFormData.right = new FormAttachment(100, 0);
		dynamoHeaderFormData.left = new FormAttachment(0, 0);
		dynamoHeaderFormData.bottom = new FormAttachment(100, 0);
		dynamoHeaderDataPanel.setLayoutData(dynamoHeaderFormData);
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
