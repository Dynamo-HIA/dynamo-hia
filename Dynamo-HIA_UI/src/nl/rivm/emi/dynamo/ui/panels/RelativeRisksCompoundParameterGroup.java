package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.ui.listeners.ScrollListener;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class RelativeRisksCompoundParameterGroup {
	private Log log = LogFactory.getLog(this.getClass().getName());
	final Group theGroup;

	public RelativeRisksCompoundParameterGroup(Composite parent,
			TypedHashMap<?> lotsOfData, DataBindingContext dataBindingContext,
			final HelpGroup helpGroup, int durationClassIndex) {
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		ScrolledComposite scrolledContainer = new ScrolledComposite(theGroup,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData formData = new FormData();
		// formData.top = new FormAttachment(label, 5);
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		scrolledContainer.setLayoutData(formData);
		FormLayout fillLayout = new FormLayout();
		scrolledContainer.setLayout(fillLayout);
		scrolledContainer.setBackground(new Color(null, 0x00, 0x00, 0xee));

		log.debug("Now for ThreeValuesPerClassParameterDataPanel");
		Composite parameterDataPanel = new ThreeValuesPerClassParameterDataPanel(
				scrolledContainer, null, lotsOfData, dataBindingContext,
				helpGroup, durationClassIndex);

		FormData parameterFormData = new FormData();

		// parameterFormData.top = new FormAttachment(label, 2);
		parameterFormData.top = new FormAttachment(0, 2);

		parameterFormData.right = new FormAttachment(100, -5);
		parameterFormData.bottom = new FormAttachment(100, -2);
		parameterFormData.left = new FormAttachment(0, 5);
		parameterDataPanel.setLayoutData(parameterFormData);
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
	}

	public void handlePlacementInContainer(Composite upperParent) {
		FormData formData = new FormData();
		if(upperParent != null){
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
