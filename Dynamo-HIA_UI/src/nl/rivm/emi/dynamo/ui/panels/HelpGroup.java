package nl.rivm.emi.dynamo.ui.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HelpGroup{
	Group theGroup = null;

	public HelpGroup(Shell shell, Composite buttonPane) {
		theGroup = new Group(shell, SWT.NONE);
		handlePlacementInContainer(theGroup, buttonPane);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 2;
		fillLayout.marginWidth = 2;
		theGroup.setLayout(fillLayout);
		theGroup.setText("Help");
		putWindowHelpGroup(theGroup);
		putFieldHelpGroup(theGroup);
		theGroup.pack();
	}

	public Group getGroup(){
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

	static private void putWindowHelpGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		// text.setSize(100, 25);
		group.setText("On this window");
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		group.setLayout(fillLayout);
		putWindowHelpText(group);
	}

	static private void putWindowHelpText(Composite parent) {
		Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		// text.setSize(100, 25);
		// text.setMessage("Massage");
		text
				.setText("Dit is de algemene, read-only, multiline text widget. En dit is nog veel meer text om de breedte te testen.");
	}

	static private void putFieldHelpGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		// text.setSize(100, 25);
		group.setText("On this field");
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		group.setLayout(fillLayout);
		putFieldHelpText(group);
	}

	static private void putFieldHelpText(Composite parent) {
		Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		text.setSize(100, 25);
		text.setText("Dit is de specifieke, read-only, multiline text widget.");
	}
}
