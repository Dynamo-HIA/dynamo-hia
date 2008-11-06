package nl.rivm.emi.dynamo.ui.panels.button;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class LotsOfButtonsPanel extends Composite {

 public LotsOfButtonsPanel(Shell shell) {
	 super(shell, SWT.NONE);
		setSize(100, 35);
		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		Button saveButton = putSaveButton(this);
		Button estimateParametersButton = putEstimateParametersButton(this,
				saveButton);
		Button viewParametersButton = putViewParametersButton(this,
				estimateParametersButton);
		Button cancelButton = putCancelButton(this);
		Button viewResultsButton = putViewResultsButton(this, cancelButton);
		putRunButton(this, viewResultsButton);
		pack();
	}

 private void setFormData() {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	private static void putRunButton(Composite panel, Button viewResultsButton) {
		FormData formData;
		Button runButton = new Button(panel, SWT.PUSH);
		runButton.setText("Run...");
		formData = new FormData();
		formData.right = new FormAttachment(viewResultsButton, -5);
		formData.bottom = new FormAttachment(100, -5);
		runButton.setLayoutData(formData);
	}

	private static Button putViewResultsButton(Composite composite, Button cancelButton) {
		FormData formData;
		Button viewResultsButton = new Button(composite, SWT.PUSH);
		viewResultsButton.setText("View Results...");
		formData = new FormData();
		formData.right = new FormAttachment(cancelButton, -15);
		formData.bottom = new FormAttachment(100, -5);
		viewResultsButton.setLayoutData(formData);
		return viewResultsButton;
	}

	private static Button putCancelButton(Composite composite) {
		FormData formData;
		Button cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText("Cancel");
		formData = new FormData();
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		cancelButton.setLayoutData(formData);
		return cancelButton;
	}

	private static Button putViewParametersButton(Composite composite,
			Button estimateParametersButton) {
		FormData formData;
		Button viewParametersButton = new Button(composite, SWT.PUSH);
		viewParametersButton.setText("View Parameters...");
		formData = new FormData();
		formData.left = new FormAttachment(estimateParametersButton, 5);
		formData.bottom = new FormAttachment(100, -5);
		viewParametersButton.setLayoutData(formData);
		return viewParametersButton;
	}

	static private Button putEstimateParametersButton(Composite composite,
			Button saveButton) {
		FormData formData;
		Button estimateParametersButton = new Button(composite, SWT.PUSH);
		estimateParametersButton.setText("Estimate Parameters");
		formData = new FormData();
		formData.left = new FormAttachment(saveButton, 15);
		formData.bottom = new FormAttachment(100, -5);
		estimateParametersButton.setLayoutData(formData);
		return estimateParametersButton;
	}

	static private Button putSaveButton(Composite panel) {
		FormData formData = new FormData();
		Button saveButton = new Button(panel, SWT.PUSH);
		saveButton.setText("Save...");
		formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		saveButton.setLayoutData(formData);
		return saveButton;
	}

}
