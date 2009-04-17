package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class TransitionDriftNettoParameterGroup {
	private static final String TREND = "Trend";
	Group theGroup;

	public TransitionDriftNettoParameterGroup(Composite parent,
			TransitionDriftNettoObject lotsOfData, DataBindingContext dataBindingContext,
			final HelpGroup helpGroup) throws DynamoConfigurationException {
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		Composite parameterDataPanel = new TransitionDriftNettoParameterDataPanel(
				theGroup, null, lotsOfData, dataBindingContext,
				helpGroup);				
				
		FormData parameterFormData = new FormData();
		parameterFormData.top = new FormAttachment(0, 2);

		parameterFormData.right = new FormAttachment(100, -5);
		parameterFormData.bottom = new FormAttachment(100, -2);
		parameterFormData.left = new FormAttachment(0, 5);
		parameterDataPanel.setLayoutData(parameterFormData);
	}

	public void handlePlacementInContainer(Composite upperParent) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(upperParent, 5);
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		formData.right = new FormAttachment(100, -5);
		theGroup.setLayoutData(formData);
	}

	public Group getGroup() {
		return theGroup;
	}
}
