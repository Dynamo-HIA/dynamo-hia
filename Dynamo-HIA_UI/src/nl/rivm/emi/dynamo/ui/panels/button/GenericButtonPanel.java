package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.selection.CancelSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.ImportSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.selection.SaveSelectionListener;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class GenericButtonPanel extends Composite {

	Button saveButton;
	Button importButton;
	DataAndFileContainer modalParent;

	// TODO: implement import button listener and actions
	public GenericButtonPanel(Shell shell) {
		super(shell, SWT.NONE);
		setSize(100, 35);
		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		saveButton = putSaveButton(this);
		importButton = putImportButton(this, saveButton);
		Button cancelButton = putCancelButton(this, importButton);
		cancelButton.addSelectionListener(new CancelSelectionListener(shell));
		pack();
	}

	private void setFormData() {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	public void setModalParent(DataAndFileContainer theParent) {
		modalParent = theParent;
		saveButton.addSelectionListener(new SaveSelectionListener(modalParent));
		importButton.addSelectionListener(new ImportSelectionListener(modalParent));
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

	static private Button putImportButton(Composite composite,
			Button leftNeighbour) {
		FormData formData;
		Button importButton = new Button(composite, SWT.PUSH);
		importButton.setText("Import");			
		formData = new FormData();
		formData.left = new FormAttachment(leftNeighbour, 15);
		formData.bottom = new FormAttachment(100, -5);
		importButton.setLayoutData(formData);
		return importButton;
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
