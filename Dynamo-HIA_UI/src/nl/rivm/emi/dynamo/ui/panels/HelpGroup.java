package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.ui.panels.help.PreconfiguredHelpGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HelpGroup {
	Group theGroup = null;
	PreconfiguredHelpGroup windowHelpGroup;
	PreconfiguredHelpGroup fieldHelpGroup;

	public HelpGroup(Shell shell, Composite buttonPane) {
		theGroup = new Group(shell, SWT.NONE);
		handlePlacementInContainer(theGroup, buttonPane);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 2;
		fillLayout.marginWidth = 2;
		theGroup.setLayout(fillLayout);
		theGroup.setText("Help");
		String[] windowTexts = {"Hellepie."};
		windowHelpGroup = new PreconfiguredHelpGroup(theGroup, "Window", windowTexts);
		String[] fieldTexts = {"FieldText0","FieldText1","FieldText2","FieldText3","FieldText4"};
		fieldHelpGroup = new PreconfiguredHelpGroup(theGroup, "Field", fieldTexts);
		theGroup.pack();
	}

	public Group getGroup() {
		return theGroup;
	}

	private void handlePlacementInContainer(Composite myComposite,
			Composite buttonPane) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(buttonPane, -5);
		formData.left = new FormAttachment(100, -155);
		myComposite.setLayoutData(formData);
	}

	public PreconfiguredHelpGroup getWindowHelpGroup() {
		return windowHelpGroup;
	}

	public PreconfiguredHelpGroup getFieldHelpGroup() {
		return fieldHelpGroup;
	}
}
