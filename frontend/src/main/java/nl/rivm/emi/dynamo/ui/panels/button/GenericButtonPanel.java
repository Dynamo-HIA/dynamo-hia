package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.listeners.selection.CloseSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.ImportSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.SaveAndCloseSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.SaveSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class GenericButtonPanel extends Composite {

	Button importButton;
	Button saveButton;
	Button saveAndCloseButton;
	Button closeButton;
	DataAndFileContainer modalParent;

	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param shell
	 */
	public GenericButtonPanel(Shell shell) {
		super(shell, SWT.NONE);
		setSize(100, 35);
		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		this.importButton = putImportButton(this);
		this.saveButton = putSaveButton(this, this.importButton);
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
	 * @throws DynamoConfigurationException
	 */
	public void setModalParent(DataAndFileContainer theParent) {
		this.modalParent = theParent;
		// Readonly behaviour.
		if (modalParent.isConfigurationFileReadOnly()) {
			importButton.setEnabled(false);
		}
		if (modalParent.isConfigurationFileReadOnly()) {
			saveButton.setEnabled(false);
		}
		if (modalParent.isConfigurationFileReadOnly()) {
			saveAndCloseButton.setEnabled(false);
		}
		// ~ Readonly behaviour.
		this.importButton.addSelectionListener(new ImportSelectionListener(
				this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(importButton);
		this.saveButton.addSelectionListener(new SaveSelectionListener(
				this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(saveButton);
		this.saveAndCloseButton
				.addSelectionListener(new SaveAndCloseSelectionListener(
						this.modalParent));
		HelpTextListenerUtil.addHelpTextListeners(saveAndCloseButton);
		this.closeButton.addSelectionListener(new CloseSelectionListener(
				theParent));
		HelpTextListenerUtil.addHelpTextListeners(closeButton);
		// } else {
		// throw new DynamoConfigurationException(
		// "Should only call setModalParent after the HelpGroup has been instanciated.");
		// }
	}

	/**
	 * 
	 * Constructs an import button
	 * 
	 * @param composite
	 * @param leftNeighbour
	 * @return Button An imput Button
	 */
	static private Button putImportButton(Composite composite) {
		FormData formData;
		Button importButton = new Button(composite, SWT.PUSH);
		importButton.setText("Import");
		formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		importButton.setLayoutData(formData);
		return importButton;
	}

	static private Button putSaveButton(Composite panel, Button leftNeighbour) {
		FormData formData = new FormData();
		Button saveButton = new Button(panel, SWT.PUSH);
		saveButton.setText("Save");
		formData = new FormData();
		formData.left = new FormAttachment(leftNeighbour, 15);
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

	/**
	 * Added to allow closing from inside the softwae.
	 * 
	 * @return closeButton
	 */
	public Button getCloseButton() {
		return closeButton;
	}
}
