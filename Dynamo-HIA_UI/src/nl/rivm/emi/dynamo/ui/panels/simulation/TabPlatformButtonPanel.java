package nl.rivm.emi.dynamo.ui.panels.simulation;

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

public class TabPlatformButtonPanel extends Composite {

	Button createButton;
	Button deleteButton;
	
	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param parent
	 */
	public TabPlatformButtonPanel(Composite parent, Composite topNeighbour) {
		super(parent, SWT.NONE);
		setSize(100, 35);
		setFormData(topNeighbour);
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		this.createButton = putCreateButton(this);
		this.deleteButton = putDeleteButton(this, this.createButton);
		pack();
	}

	private void setFormData(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		//formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	/**
	 * 
	 * Sets the selection listeners
	 * 
	 * @param platform
	 */
	public void setSelectionListeners(TabPlatform platform) {
		this.createButton.addSelectionListener(new CreateSelectionListener(platform));
		this.deleteButton.addSelectionListener(new DeleteSelectionListener(platform));
	}

	static private Button putCreateButton(Composite parent) {
		FormData formData = new FormData();
		Button createButton = new Button(parent, SWT.PUSH);
		createButton.setText("create");
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -5);
		createButton.setLayoutData(formData);
		return createButton;
	}

	
	/**
	 * 
	 * Constructs an delete button 
	 * 
	 * @param composite
	 * @param leftNeighbour
	 * @return Button An imput Button
	 */
	static private Button putDeleteButton(Composite composite,
			Button leftNeighbour) {
		FormData formData;
		Button deleteButton = new Button(composite, SWT.PUSH);
		deleteButton.setText("delete");			
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(leftNeighbour, 15);
		formData.bottom = new FormAttachment(100, -5);
		deleteButton.setLayoutData(formData);
		return deleteButton;
	}
}
