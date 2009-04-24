package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousObject;
import nl.rivm.emi.dynamo.ui.parametercontrols.ScrollListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class RiskFactorContinuousParameterGroup {
	Group theGroup;

	public RiskFactorContinuousParameterGroup(Composite parent,
			RiskFactorContinuousObject riskFactorContinuousObject,
			DataBindingContext dataBindingContext, final HelpGroup helpGroup) {
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		ScrolledComposite scrolledContainer = new ScrolledComposite(theGroup,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormLayout formLayout4Scroll = new FormLayout();
		scrolledContainer.setLayout(formLayout4Scroll);
		scrolledContainer.setBackground(new Color(null, 0x00, 0x00, 0xee));

		Composite cutoffValuesDataPanel = new CutoffDefinitionsDataPanel(
				scrolledContainer, null, riskFactorContinuousObject, dataBindingContext,
				helpGroup);
		FormData cutoffValueFormData = new FormData();
		cutoffValueFormData.top = new FormAttachment(0, 2);
		cutoffValueFormData.right = new FormAttachment(100, -5);
		cutoffValueFormData.left = new FormAttachment(0, 5);
		cutoffValueFormData.bottom = new FormAttachment(100, -2);
		cutoffValuesDataPanel.setLayoutData(cutoffValueFormData);
		FormData scrolledContainerFormData = new FormData();
		scrolledContainerFormData.top = new FormAttachment(0, 5);
		scrolledContainerFormData.left = new FormAttachment(0, 0);
		scrolledContainerFormData.right = new FormAttachment(100, 0);
		scrolledContainer.setContent(cutoffValuesDataPanel);
		scrolledContainer.setExpandHorizontal(true);
		scrolledContainer.setExpandVertical(true);
		scrolledContainer.setMinSize(cutoffValuesDataPanel.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		Control[] controls = cutoffValuesDataPanel.getChildren();
		ScrollListener listener = new ScrollListener(scrolledContainer);
		for (int i = 0; i < controls.length; i++) {
			controls[i].addListener(SWT.Activate, listener);
		}
		Composite referenceValueDataPanel = new ReferenceValueDataPanel(
				theGroup, scrolledContainer, riskFactorContinuousObject, dataBindingContext,
				helpGroup);
		FormData referenceValueFormData = new FormData();
		referenceValueFormData.top = new FormAttachment(100, -22);
		referenceValueFormData.right = new FormAttachment(100, -5);
		referenceValueFormData.left = new FormAttachment(0, 5);
		referenceValueFormData.bottom = new FormAttachment(100, -2);
		referenceValueDataPanel.setLayoutData(referenceValueFormData);
		// Leave room for the neighbour below.
		scrolledContainerFormData.bottom = new FormAttachment(referenceValueDataPanel, -2);
		scrolledContainer.setLayoutData(scrolledContainerFormData);
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
