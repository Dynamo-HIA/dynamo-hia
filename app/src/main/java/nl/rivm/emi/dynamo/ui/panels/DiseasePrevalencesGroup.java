package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DiseasePrevalencesGroup {
	final Group theGroup;

	public DiseasePrevalencesGroup(Shell shell, TypedHashMap<?> lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
//		String[] entityArray = Util.deriveEntityLabelAndValueFromRiskSourceNode(selectedNode);
//		EntityNamePanel entityNameGroup = new EntityNamePanel(theGroup,
//				entityArray[0], entityArray[1], null);
//		entityNameGroup.putInContainer();
		EntityInDefaultDirNamePanel entityNameGroup = 
			new EntityInDefaultDirNamePanel(theGroup,
				selectedNode, helpGroup);
		entityNameGroup.handlePlacementInContainer();
		DiseasePrevalencesParameterGroup parameterGroup = new DiseasePrevalencesParameterGroup(
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
