package nl.rivm.emi.dynamo.ui.panels;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorContinuousParameterGroup {
	Group theGroup;

	public RiskFactorContinuousParameterGroup(Composite parent,
			RiskFactorContinuousObject riskFactorContinuousObject,
			DataBindingContext dataBindingContext, final HelpGroup helpGroup) {
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		Composite referenceValueDataPanel = new ReferenceValueDataPanel(
				theGroup, null, riskFactorContinuousObject, dataBindingContext,
				helpGroup);
		FormData referenceClassFormData = new FormData();
		referenceClassFormData.top = new FormAttachment(100, -22);
		referenceClassFormData.right = new FormAttachment(100, -5);
		referenceClassFormData.left = new FormAttachment(0, 5);
		referenceClassFormData.bottom = new FormAttachment(100, -2);
		referenceValueDataPanel.setLayoutData(referenceClassFormData);
	}

	public void handlePlacementInContainer(Composite upperParent) {
		FormData formData = new FormData();
		if (upperParent != null) {
			formData.top = new FormAttachment(upperParent, 5);
		} else {
			formData.top = new FormAttachment(0, 5);
		}
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		formData.right = new FormAttachment(100, -5);
		theGroup.setLayoutData(formData);
	}

	public Group getGroup() {
		return theGroup;
	}
}
