package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.objects.PopulationSizeObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.ui.parametercontrols.ScrollListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RiskFactorCategoricalParameterGroup {
	Group theGroup;

	public RiskFactorCategoricalParameterGroup(Composite parent,
			RiskFactorCategoricalObject riskFactorCategoricalObject,
			DataBindingContext dataBindingContext, final HelpGroup helpGroup) {
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		ScrolledComposite scrolledContainer = new ScrolledComposite(theGroup,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormLayout formLayout4Scroll = new FormLayout();
		scrolledContainer.setLayout(formLayout4Scroll);
		scrolledContainer.setBackground(new Color(null, 0x00, 0x00, 0xee));
		Composite parameterDataPanel = new ClassDefinitionsDataPanel(
				scrolledContainer, null, riskFactorCategoricalObject,
				dataBindingContext, helpGroup);
		FormData parameterFormData = new FormData();
		parameterFormData.top = new FormAttachment(0, 2);
		parameterFormData.right = new FormAttachment(100, -5);
		parameterFormData.left = new FormAttachment(0, 5);
		parameterFormData.bottom = new FormAttachment(100,
				-2);
		parameterDataPanel.setLayoutData(parameterFormData);
		FormData scrolledContainerFormData = new FormData();
		scrolledContainerFormData.top = new FormAttachment(0, 5);
		scrolledContainerFormData.right = new FormAttachment(100, 0);
		scrolledContainerFormData.left = new FormAttachment(0, 0);
		scrolledContainer.setContent(parameterDataPanel);
		scrolledContainer.setExpandHorizontal(true);
		scrolledContainer.setExpandVertical(true);
		scrolledContainer.setMinSize(parameterDataPanel.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		Control[] controls = parameterDataPanel.getChildren();
		ScrollListener listener = new ScrollListener(scrolledContainer);
		for (int i = 0; i < controls.length; i++) {
			controls[i].addListener(SWT.Activate, listener);
		}
		Composite referenceClassDataPanel = new ReferenceClassDataPanel(
				theGroup, scrolledContainer, riskFactorCategoricalObject,
				dataBindingContext, helpGroup);
		FormData referenceClassFormData = new FormData();
		//referenceClassFormData.top = new FormAttachment(scrolledContainer, 2);
		referenceClassFormData.top = new FormAttachment(100, -22);
		referenceClassFormData.right = new FormAttachment(100, -5);
		referenceClassFormData.left = new FormAttachment(0, 5);
		referenceClassFormData.bottom = new FormAttachment(100, -2);
		referenceClassDataPanel.setLayoutData(referenceClassFormData);
		// Leave room for the neighbour below.
		scrolledContainerFormData.bottom = new FormAttachment(referenceClassDataPanel, -2);
		scrolledContainer.setLayoutData(scrolledContainerFormData);
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