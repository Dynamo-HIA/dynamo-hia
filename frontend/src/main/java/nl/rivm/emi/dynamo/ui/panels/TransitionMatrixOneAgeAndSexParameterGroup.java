package nl.rivm.emi.dynamo.ui.panels;


import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class TransitionMatrixOneAgeAndSexParameterGroup {
	/**
	 * Extending Group is not allowed.
	 */
	Group theGroup;

//	public TransitionMatrixOneAgeAndSexParameterGroup(Composite tempContainer,
//			Object object, DataBindingContext dataBindingContext,
//			BaseNode selectedNode, HelpGroup helpPanel) {
//		// TODO Auto-generated constructor stub
//	}

	
	public TransitionMatrixOneAgeAndSexParameterGroup(Composite parent,
			TypedHashMap<TransitionSource> transitionMatrixModelObjectPart,
			DataBindingContext dataBindingContext, BaseNode selectedNode,  
			final HelpGroup helpGroup){
		theGroup = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		 Composite parameterDataPanel = new TransitionMatrixParameterPanel(
				theGroup, null, transitionMatrixModelObjectPart,
				dataBindingContext, helpGroup);
		FormData parameterFormData = new FormData();
		parameterFormData.top = new FormAttachment(0, 2);
		parameterFormData.right = new FormAttachment(100, -2);
		parameterFormData.left = new FormAttachment(0, 2);
		parameterFormData.bottom = new FormAttachment(100,
				-2);
		parameterDataPanel.setLayoutData(parameterFormData);
	}



	public Group getGroup() {
		return theGroup;
	}
}
