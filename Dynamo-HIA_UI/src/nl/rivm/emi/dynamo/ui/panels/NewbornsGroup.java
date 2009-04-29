package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
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

public class NewbornsGroup {
	Group theGroup;

	public NewbornsGroup(Shell shell, NewbornsObject newbornsObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup, DataAndFileContainer modalParent) 
			throws DynamoConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		// Set the population file name
		String[] entityArray = Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
		EntityNamePanel entityNameGroup = new EntityNamePanel(theGroup,
				entityArray[0], entityArray[1], null);
		entityNameGroup.putInContainer();

		// Show the Sex Ratio and
		// the Starting Year and 'Update' button				
		SexRatioAndStartingYearPanel nestedGroup = 
			new SexRatioAndStartingYearPanel(theGroup, newbornsObject,
				dataBindingContext, helpGroup, modalParent);
		nestedGroup.putNextInContainer(entityNameGroup.group, 30, nestedGroup.group);
		
		// Show the Year-Number table
		NewbornsParameterGroup parameterGroup = new NewbornsParameterGroup(
				theGroup, newbornsObject, dataBindingContext, helpGroup, 
				nestedGroup.getStartingYearModifyListener());
		parameterGroup.handlePlacementInContainer(nestedGroup.groupStartingYear);
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
