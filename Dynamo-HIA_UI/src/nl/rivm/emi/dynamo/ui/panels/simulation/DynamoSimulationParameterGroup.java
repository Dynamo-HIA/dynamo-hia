package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class DynamoSimulationParameterGroup {
	Group group;	
		
	public DynamoSimulationParameterGroup(Composite parent,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			final HelpGroup helpGroup) throws DynamoConfigurationException {
		group = new Group(parent, SWT.NONE); // parent group
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
				
		// Panel that contains the simulation header information
		DynamoHeaderParameterGroup dynamoHeaderParameterGroup =
			new DynamoHeaderParameterGroup(group, dynamoSimulationObject, 
					dataBindingContext, selectedNode, helpGroup);	
		dynamoHeaderParameterGroup.putFirstInContainer(250);
		
		// Panel that contains the tabs
		DynamoTabsParameterGroup dynamoTabsParameterGroup =
			new DynamoTabsParameterGroup(group, selectedNode, 
					dynamoSimulationObject,
					dataBindingContext, helpGroup);
		dynamoTabsParameterGroup.handleNextInContainer(dynamoHeaderParameterGroup.group);
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
