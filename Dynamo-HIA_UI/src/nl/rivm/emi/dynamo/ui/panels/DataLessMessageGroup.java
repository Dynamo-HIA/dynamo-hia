package nl.rivm.emi.dynamo.ui.panels;

import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DataLessMessageGroup {
	Group theGroup;

	public DataLessMessageGroup(Shell shell, BaseNode selectedNode, Set<String> messageLineSet) throws DynamoConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		EntityInDefaultDirNamePanel entityNamePanel = new EntityInDefaultDirNamePanel(theGroup,
				selectedNode, null);
		entityNamePanel.handlePlacementInContainer();
		DataLessMessagePanel messagePanel = new DataLessMessagePanel(
				theGroup, messageLineSet);
		messagePanel.handlePlacementInContainer(entityNamePanel.group);
	}

	public void setFormData(Composite lowerNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -2);
		formData.bottom = new FormAttachment(lowerNeighbour, -5);
		theGroup.setLayoutData(formData);
	}
}
