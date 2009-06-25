package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.selection.CloseSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.ImportSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.NoDataSaveSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.SimpleCancelSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.TransitionDriftNettoSaveSelectionListener;
import nl.rivm.emi.dynamo.ui.main.AbstractHelplessModal;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NoImportButtonPanel extends Composite {
	Button saveButton;
	Button cancelButton;
	AbstractHelplessModal modalParent;
	
	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param shell
	 */
	public NoImportButtonPanel(Shell shell) {
		super(shell, SWT.NONE);
		setSize(100, 35);
		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		this.saveButton = putSaveButton(this);
		cancelButton = putCancelButton(this, this.saveButton);
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
		this.saveButton.addSelectionListener(new NoDataSaveSelectionListener(this.modalParent));
		cancelButton.addSelectionListener(new SimpleCancelSelectionListener(this.getShell()));
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

	
	private static Button putCancelButton(Composite composite,
			Button leftNeighbour) {
		FormData formData;
		Button cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText("Cancel");
		formData = new FormData();
		formData.left = new FormAttachment(leftNeighbour, 15);
		formData.bottom = new FormAttachment(100, -5);
		cancelButton.setLayoutData(formData);
		return cancelButton;
	}

}
