package nl.rivm.emi.dynamo.ui.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EntityNamePanel{
	Group group;
	Label nameLabel;
	HelpGroup theHelpGroup;
	
	 public EntityNamePanel(Composite parent, String entityLabel, String entityValue) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		Label label = new Label(group, SWT.LEFT);
		label.setText(entityLabel + ":");
		nameLabel = new Label(group, SWT.LEFT);
		nameLabel.setText(entityValue);
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 5);
		labelFormData.right = new FormAttachment(0, 100);
		labelFormData.bottom = new FormAttachment(100, -5);
		label.setLayoutData(labelFormData);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(label, 2);
		textFormData.right = new FormAttachment(100, -5);
		textFormData.bottom = new FormAttachment(100, -5);
		nameLabel.setLayoutData(textFormData);
	}
	 
	public void putInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public void putFirstInContainer(int height) {
//		int hijgt = group.getParent().getBounds().height;
//		int wis = group.getParent().getBounds().width;
//		group.setText("Ouder-height: " + hijgt + " width: " + wis);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);
	}
	
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
