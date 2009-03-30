package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.RunButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DynamoSimulationGroup {
	Log log = LogFactory.getLog(this.getClass().getName());
	Group theGroup;

	public DynamoSimulationGroup(Shell shell, DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup, SimulationModal simulationModal) 
			throws ConfigurationException {
		log.fatal("selectedNode-label: " + selectedNode.deriveNodeLabel());
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		
		// Create the parameter group and place it in the screen container
		// The parameter group of the simulation consists of two group parts
		DynamoSimulationParameterGroup parameterGroup = new DynamoSimulationParameterGroup(
				theGroup, dynamoSimulationObject, dataBindingContext, 
				selectedNode, helpGroup);
		parameterGroup.putFirstInContainer(300);
		
		// The third panel that contains the Run button
		RunButtonPanel runButtonGroup = 
			new RunButtonPanel(this.theGroup, simulationModal);
		runButtonGroup.putLastInContainer(parameterGroup.group);
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
