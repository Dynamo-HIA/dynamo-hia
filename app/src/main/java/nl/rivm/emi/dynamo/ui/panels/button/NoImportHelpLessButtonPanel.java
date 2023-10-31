package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.selection.NoDataSaveAndCloseSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.NoDataSaveSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.SimpleCancelSelectionListener;
import nl.rivm.emi.dynamo.ui.main.base.AbstractHelplessModal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NoImportHelpLessButtonPanel extends Composite {
	Button saveButton;
	Button saveAndCloseButton;
	Button closeButton;
	AbstractHelplessModal modalParent;

	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param shell
	 */
	public NoImportHelpLessButtonPanel(Shell shell) {
		super(shell, SWT.NONE);
		setSize(100, 35);
		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		this.saveButton = putSaveButton(this);
		this.saveAndCloseButton = putSaveAndCloseButton(this, this.saveButton);
		this.closeButton = putCloseButton(this, this.saveAndCloseButton);
		pack();
	}

	private void setFormData() {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	/**
	 * 
	 * Sets the parent of the modal
	 * 
	 * @param theParent
	 */
	public void setModalParent(AbstractHelplessModal theParent) {
		this.modalParent = theParent;
		this.saveButton.addSelectionListener(new NoDataSaveSelectionListener(
				this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(saveButton);
		this.saveAndCloseButton
				.addSelectionListener(new NoDataSaveAndCloseSelectionListener(
						this.getShell(), this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(saveAndCloseButton);
		closeButton.addSelectionListener(new SimpleCancelSelectionListener(
				this.getShell()));
		HelpTextListenerUtil.addHelpTextListeners(closeButton);
	}

	static private Button putSaveButton(Composite panel) {
		FormData formData = new FormData();
		Button saveButton = new Button(panel, SWT.PUSH);
		saveButton.setText("Save");
		formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		saveButton.setLayoutData(formData);
		return saveButton;
	}

	static private Button putSaveAndCloseButton(Composite panel,
			Button leftNeighbour) {
		FormData formData = new FormData();
		Button saveAndCloseButton = new Button(panel, SWT.PUSH);
		saveAndCloseButton.setText("Save and close");
		formData = new FormData();
		formData.left = new FormAttachment(leftNeighbour, 15);
		formData.bottom = new FormAttachment(100, -5);
		saveAndCloseButton.setLayoutData(formData);
		return saveAndCloseButton;
	}

	private static Button putCloseButton(Composite composite,
			Button leftNeighbour) {
		FormData formData;
		Button closeButton = new Button(composite, SWT.PUSH);
		closeButton.setText("Close");
		formData = new FormData();
		formData.left = new FormAttachment(leftNeighbour, 15);
		formData.bottom = new FormAttachment(100, -5);
		closeButton.setLayoutData(formData);
		return closeButton;
	}

}
