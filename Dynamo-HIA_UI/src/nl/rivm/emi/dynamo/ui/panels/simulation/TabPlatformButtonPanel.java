package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.ui.listeners.ButtonFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.CreateSelectionListener;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.DeleteSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Button panel for the tabplatfrom Contains the create and delete buttons for
 * the nested tabs
 * 
 * @author schutb
 * 
 */
public class TabPlatformButtonPanel extends Composite {

	Button createButton;
	Button deleteButton;
	HelpGroup helpGroup;

	/**
	 * 
	 * Constructs the panel with buttons
	 * 
	 * @param parent
	 * @param helpGroup
	 *            TODO
	 */
	public TabPlatformButtonPanel(Composite parent, Composite topNeighbour,
			HelpGroup helpGroup) {
		super(parent, SWT.NONE);
		this.helpGroup = helpGroup;
		setSize(100, 35);
		setFormData(topNeighbour);
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		this.createButton = putCreateButton(this);
		HelpTextListenerUtil.addHelpTextListeners(createButton);
		this.deleteButton = putDeleteButton(this, this.createButton);
		HelpTextListenerUtil.addHelpTextListeners(deleteButton);
		pack();
	}

	private void setFormData(Composite topNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		// formData.bottom = new FormAttachment(100, -5);
		setLayoutData(formData);
	}

	/**
	 * 
	 * Sets the selection listeners
	 * 
	 * @param platform
	 */
	public void setSelectionListeners(TabPlatform platform) {
		this.createButton.addSelectionListener(new CreateSelectionListener(
				platform, helpGroup.getTheModal()));
		this.deleteButton.addSelectionListener(new DeleteSelectionListener(
				platform, helpGroup.getTheModal()));
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
