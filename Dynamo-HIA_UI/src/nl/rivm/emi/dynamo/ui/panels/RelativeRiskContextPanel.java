package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

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

public class RelativeRiskContextPanel {
	private static final String FROM = "From ";
	private static final String TO = "To ";
	Group group;
	Label nameLabel;

	Log log = LogFactory.getLog(this.getClass().getName());
	
	public RelativeRiskContextPanel(Composite parent, BaseNode riskSourceNode,
			BaseNode selectedNode) throws ConfigurationException, DynamoInconsistentDataException {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		String[] entityArray = null;
		if (riskSourceNode != null) {
			entityArray = Util.deriveEntityLabelAndValueFromRiskSourceNode(riskSourceNode);
		} else {
			entityArray = Util
					.deriveRiskSourceTypeAndLabelFromSelectedNode(selectedNode);
		}
		String[] anotherEntityArray = Util
		.deriveGrandParentEntityLabelAndValue(selectedNode);
		EntityNamePanel entityNameGroup = new EntityNamePanel(group,
				anotherEntityArray[0], anotherEntityArray[1], "");
		entityNameGroup.putFirstInContainer(30);
		EntityNamePanel riskSourcePanel = new EntityNamePanel(group,
				entityArray[0], entityArray[1], "");
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