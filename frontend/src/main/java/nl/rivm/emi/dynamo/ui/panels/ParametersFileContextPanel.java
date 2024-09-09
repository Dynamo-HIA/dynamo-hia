package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.ParentNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ParametersFileContextPanel {
	public Group group;
	Label nameLabel;

	Log log = LogFactory.getLog(this.getClass().getName());

	public ParametersFileContextPanel(Composite parent,
			BaseNode riskSourceNode, BaseNode selectedNode)
			throws ConfigurationException, DynamoInconsistentDataException {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		ParentNode parentNode = ((ChildNode) selectedNode).getParent();
		ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
		String simulationName = ((BaseNode) grandParentNode).deriveNodeLabel();
		EntityNamePanel entityNameGroup = new EntityNamePanel(group,
				"Simulation", simulationName, "");
		entityNameGroup.putFirstInContainer(30);
		EntityNamePanel riskSourcePanel = new EntityNamePanel(group,
				"Filename", ((BaseNode) selectedNode).getPhysicalStorage()
						.getName(), "");
		riskSourcePanel.putLastInContainer(entityNameGroup.group);
	}

	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
