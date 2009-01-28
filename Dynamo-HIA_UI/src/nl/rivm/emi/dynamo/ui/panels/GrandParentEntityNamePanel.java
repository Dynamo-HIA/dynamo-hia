package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GrandParentEntityNamePanel implements HelpController{
	Group group;
	Label nameLabel;
	HelpGroup theHelpGroup;
	
	 public GrandParentEntityNamePanel(Composite parentComposite, BaseNode selectedNode, final HelpGroup theHelpGroup) {
		group = new Group(parentComposite, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		BaseNode startNode = selectedNode;
		// The modals have dual use, for new and existing files. This corrects the level.
		if(selectedNode instanceof FileNode){
			startNode = (BaseNode)((ChildNode)selectedNode).getParent();
		}
		ParentNode parentNode = ((ChildNode)startNode).getParent();
		String parentLabel = ((BaseNode)parentNode).toString();
		ParentNode grandParentNode = ((ChildNode)parentNode).getParent();
		String grandParentLabel = ((BaseNode)grandParentNode).toString();
		Label label = new Label(group, SWT.LEFT);
		label.setText(grandParentLabel.substring(0, grandParentLabel.length()-1) + ":");
		nameLabel = new Label(group, SWT.LEFT);
		nameLabel.setText(parentLabel);
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
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;		
	}
}
