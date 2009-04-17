package nl.rivm.emi.dynamo.ui.panels;

import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class DataLessMessagePanel {
	Group theGroup;

	public DataLessMessagePanel(Composite parent, Set<String> messageLineSet)
			throws DynamoConfigurationException {
		theGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		theGroup.setLayout(layout);
		for(String messageLine: messageLineSet){
		Text messageLineText = new Text(theGroup, SWT.MULTI);
		messageLineText.setEditable(false);
		messageLineText
				.setText(messageLine);
		}
	}

	public void handlePlacementInContainer(Composite upperParent) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(upperParent, 5);
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		formData.right = new FormAttachment(100, -5);
		theGroup.setLayoutData(formData);
	}

	public Group getGroup() {
		return theGroup;
	}
}
