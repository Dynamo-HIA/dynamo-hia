package nl.rivm.emi.dynamo.ui.panels;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EntityNamePanel {
	Log log = LogFactory.getLog(this.getClass().getName());
	final Group group;
	final Label nameLabel;
	HelpGroup theHelpGroup;

	public EntityNamePanel(Composite parent, String entityLabel,
			String entityValue, String prefix) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		boolean prefixed = (prefix != null);
		final Label label = new Label(group, SWT.LEFT);
		String labelContent = null;
		if (!prefixed) {
			labelContent = (entityLabel.substring(0, entityLabel.length()))
					.replace('_', ' ');
		} else {
			labelContent = prefix
					+ " "
					+ (entityLabel.substring(0, entityLabel.length())).replace(
							'_', ' ');
		}
		label.setText(labelContent + ":");
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

	/**
	 * 
	 * Place the first group in the container
	 * 
	 * @param height
	 */
	public void putFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);
	}

	/**
	 * 
	 * Place the middle group in the container
	 * 
	 * @param topNeighbour
	 * @param height
	 */
	public void putMiddleInContainer(Composite topNeighbour, int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(6, 5 + height);
		group.setLayoutData(formData);
	}

	/**
	 * 
	 * Place the last (and second) group in the container
	 * 
	 * @param topNeighbour
	 */
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
