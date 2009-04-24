package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EntityInDefaultDirNamePanel implements HelpController{
	Group group;
	Label nameLabel;
	HelpGroup theHelpGroup;
	
	 public EntityInDefaultDirNamePanel(Composite parent, BaseNode selectedNode, final HelpGroup theHelpGroup) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		BaseNode startNode = selectedNode;
		BaseNode standardDirNode = null;
		if(selectedNode instanceof FileNode){
			standardDirNode = (BaseNode)((ChildNode)selectedNode).getParent();
		} else {
			standardDirNode = startNode;
		}
		// String standardDirLabel = ((BaseNode)standardDirNode).toString();
		ParentNode containerInstanceDirNode = ((ChildNode)standardDirNode).getParent();
		String containerInstanceDirLabel = ((BaseNode)containerInstanceDirNode).toString();
		ParentNode containerClassDirNode = ((ChildNode)containerInstanceDirNode).getParent();
		String containerClassDirLabel = ((BaseNode)containerClassDirNode).toString();
		Label label = new Label(group, SWT.LEFT);
		String labelText = containerClassDirLabel.substring(0, containerClassDirLabel.length()-1) + ":"; 
		label.setText(labelText.replace('_' , ' '));
		nameLabel = new Label(group, SWT.LEFT);
		nameLabel.setText(containerInstanceDirLabel);
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 5);
		labelFormData.right = new FormAttachment(0, 100);
		labelFormData.bottom = new FormAttachment(100, -5);
		label.setLayoutData(labelFormData);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(label, 2);
		textFormData.right = new FormAttachment(100, -5);
		nameLabel.setLayoutData(textFormData);
	}
	 
	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;		
	}
	
	public Group getGroup(Group group) {
		return this.group;		
	}	
}
