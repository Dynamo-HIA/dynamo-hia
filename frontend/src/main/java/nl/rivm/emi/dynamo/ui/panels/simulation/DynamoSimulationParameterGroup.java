package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Defines the simulation panel header parameter group
 * 
 * @author schutb
 *
 */
public class DynamoSimulationParameterGroup {
	Composite group;	
		
	public DynamoSimulationParameterGroup(Composite parent,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			final HelpGroup helpGroup) throws ConfigurationException {
		group = new Composite(parent, SWT.NONE); // parent group
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
				
		// Panel that contains the simulation header information
		DynamoHeaderParameterGroup dynamoHeaderParameterGroup =
			new DynamoHeaderParameterGroup(group, dynamoSimulationObject, 
					dataBindingContext, selectedNode, helpGroup);	
		dynamoHeaderParameterGroup.putFirstInContainer(150); // 20090709 Original 250.
		
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

	public void putFirstInContainer(int height, Composite bottomNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		// This changes the width of the panel, but this needs to fit
		// into the container
		// HB changed from 100 to 120
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(bottomNeighbour, -5);
		group.setLayoutData(formData);		
	}

}
