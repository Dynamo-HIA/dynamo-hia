package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class RelativeRiskPanel implements HelpController{
	Group group;
	Label nameLabel;
	HelpGroup theHelpGroup;
	
	 public RelativeRiskPanel(Composite parent, BaseNode riskSourceNode, BaseNode selectedNode) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		EntityNamePanel riskSourcePanel = new EntityNamePanel(group,
				riskSourceNode, null);
		riskSourcePanel.putFirstInContainer(30);
		GrandParentEntityNamePanel entityNameGroup = new GrandParentEntityNamePanel(
				group, selectedNode, null);
		entityNameGroup.putLastInContainer(riskSourcePanel.group);
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
}
