package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
////import org.eclipse.swt.layout.FormAttachment;
////import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Trialog modal that contains an option to specify radio buttons. The number of
 * radio buttons is adjustable.
 * 
 * @author schutb
 * 
 */
public class InputBulletsTrialog extends ImportExtendedInputTrialog {

	protected Control bulletArea;

	/** Three type radiobuttons. */
	private Button[] radioButtons = new Button[3];

	/* Initialized separately because setSelection does not generate an event. */
	private String selectedBulletButtonName;

	protected BulletButtonNamesEnum myBulletButtonNamesEnum;

	private BulletButtonNamesEnum bulletButtonNamesEnum;

	private BulletButtonNamesEnum bulletButtonNamesEnum2;

	private BulletButtonNamesEnum bulletButtonNamesEnum3;

	public InputBulletsTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator, 
			BulletButtonNamesEnum bulletButtonNamesEnum, 
			BulletButtonNamesEnum bulletButtonNamesEnum2, 
			BulletButtonNamesEnum bulletButtonNamesEnum3) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.bulletButtonNamesEnum = bulletButtonNamesEnum;
		this.bulletButtonNamesEnum2 = bulletButtonNamesEnum2;
		this.bulletButtonNamesEnum3 = bulletButtonNamesEnum3;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(1808));
		applyDialogFont(composite);

		initializeDialogUnits(composite);

		// Create the components of the bullet trialog
		this.dialogArea = createDialogArea(composite);
		this.bulletArea = createBulletArea(composite);
		this.buttonBar = createButtonBar(composite);

		return composite;
	}

	protected Control createBulletArea(Composite parent) {
		Composite composite = new Composite(parent, 0);

		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(12);
		layout.marginHeight = convertVerticalDLUsToPixels(12);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(9);
		layout.verticalSpacing = convertVerticalDLUsToPixels(9);
		composite.setLayout(layout);
		GridData data = new GridData(132);

		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		createBulletsForBulletArea(composite);

		return composite;
	}

	private void createBulletsForBulletArea(Composite composite) {

		// First radio button
		//this.myBulletButtonNamesEnum = bulletButtonNamesEnum;
		createRadioButton(composite, 0, bulletButtonNamesEnum);

		// Second radio button
		//this.myBulletButtonNamesEnum = this.bulletButtonNamesEnum2;
		createRadioButton(composite, 1, bulletButtonNamesEnum2);

		// Third radio button
		//this.myBulletButtonNamesEnum = this.bulletButtonNamesEnum3;
		createRadioButton(composite, 2, bulletButtonNamesEnum3);
		
		// Set the default radio button
		this.radioButtons[0].setSelection(true);

	}

	// id is the radio button number
	// label is "categorical"
	protected void createRadioButton(Composite parent, final int id,
			final BulletButtonNamesEnum bulletButtonNamesEnum) {
		// What is the purpose ?????
		((GridLayout) parent.getLayout()).numColumns += 1;

		// The radio button idiom
		this.radioButtons[id] = new Button(parent, SWT.RADIO);
		this.radioButtons[id].setText(bulletButtonNamesEnum.getBulletButtonName());
		this.radioButtons[id].addListener(SWT.Selection, new Listener() {
			// /TODORootElementNamesEnum myRootElementNamesEnum =
			// this.InputBulletsTrialog.myRootElementNamesEnum

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					InputBulletsTrialog.this.selectedBulletButtonName = 
						bulletButtonNamesEnum
							.getBulletButtonName();

					setSelectionOtherRadioButtons(id);

				}
			}
		});		
		
		/* already defined in the grid
		FormData radio1FormData = new FormData();
		radio1FormData.left = new FormAttachment(0, 15);
		radio1FormData.right = new FormAttachment(100, -15);
		radio1FormData.top = new FormAttachment(0, 10);
		this.radioButtons[id].setLayoutData(radio1FormData);
		*/
		
		//this.radiobuttons.put(new Integer(id), button); // TODO????
	}

	
	/**
	 * 
	 * Sets the selection of the other radio buttons 
	 * than the selected to false
	 * 
	 * @param id
	 */
	public void setSelectionOtherRadioButtons(int id) {
		for (int i = 0; i < 3; i++) {
			if (i != id)
				this.radioButtons[i].setSelection(false);
		}
	}
	
	public String getSelectedBulletButtonName() {
		return selectedBulletButtonName;
	}

}