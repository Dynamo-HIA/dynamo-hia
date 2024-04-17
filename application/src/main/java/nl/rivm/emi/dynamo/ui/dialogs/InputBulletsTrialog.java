package nl.rivm.emi.dynamo.ui.dialogs;

import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Trialog modal that contains an option to specify radio buttons. The number of
 * radio buttons is adjustable.
 * 
 * @author schutb
 * 
 *         20090518 mondeelr Opposite to the comment above, the number of radio
 *         buttons was fixed at three. It has now been refactored to enable a
 *         variable number of radio buttons.
 */

abstract public class InputBulletsTrialog extends ImportExtendedInputTrialog {

	private String bulletsLabel = "InitialLabel";

	protected Control bulletArea;

	/** A variable number of radiobuttons. */
	protected Button[] radioButtons; // = new Button[3];

	/* Initialized separately because setSelection does not generate an event. */
	protected int selectedBulletButtonIndex;

	protected BulletButtonNamesEnum[] bulletButtonNamesEnums;

	protected InputBulletsTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue,
			IInputValidator validator, String bulletsLabel,
			BulletButtonNamesEnum[] bulletButtonNamesEnums) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.bulletsLabel = bulletsLabel;
		this.bulletButtonNamesEnums = bulletButtonNamesEnums;
		this.radioButtons = new Button[bulletButtonNamesEnums.length];
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
		layout.marginWidth = convertHorizontalDLUsToPixels(7);
		layout.marginHeight = convertVerticalDLUsToPixels(5);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(5);
		layout.verticalSpacing = convertVerticalDLUsToPixels(10);
		composite.setLayout(layout);
		GridData data = new GridData(10);

		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		// Create the "Transition type" label
		createTypeLabel(composite);
		// Create the bullets
		createBulletsForBulletArea(composite);

		return composite;
	}

	private void createBulletsForBulletArea(Composite composite) {
		int count = 0;
		for (BulletButtonNamesEnum anEnum : bulletButtonNamesEnums) {
			createRadioButton(composite, count, anEnum);
			count++;
		}
		initializeRadioButtons();
	}

	abstract protected void initializeRadioButtons();

	// id is the radio button number
	// label is "categorical"
	protected void createRadioButton(Composite parent, final int id,
			final BulletButtonNamesEnum bulletButtonNamesEnum) {
		// What is the purpose ?????
		((GridLayout) parent.getLayout()).numColumns += 1;

		// The radio button idiom
		this.radioButtons[id] = new Button(parent, SWT.RADIO);
		this.radioButtons[id].setText(bulletButtonNamesEnum
				.getBulletButtonName());
		this.radioButtons[id].addListener(SWT.Selection, new Listener() {
			// /TODORootElementNamesEnum myRootElementNamesEnum =
			// this.InputBulletsTrialog.myRootElementNamesEnum

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					selectedBulletButtonIndex = id;
					deselectOtherRadioButtons(id);
				}
			}
		});
	}

	private void createTypeLabel(Composite composite) {
		((GridLayout) composite.getLayout()).numColumns += 1;
		Label riskFactorNameLabel = new Label(composite, SWT.NONE);
		riskFactorNameLabel.setText(bulletsLabel);
	}

	/**
	 * 
	 * Sets the selection of the other radio buttons than the selected to false
	 * 
	 * @param id
	 */
	public void deselectOtherRadioButtons(int id) {
		for (int i = 0; i < bulletButtonNamesEnums.length; i++) {
			if (i != id)
				this.radioButtons[i].setSelection(false);
		}
	}

	public String getSelectedBulletButtonName() {
		return bulletButtonNamesEnums[selectedBulletButtonIndex].getBulletButtonName();
	}

	public String getSelectedRootElementName() {
		return bulletButtonNamesEnums[selectedBulletButtonIndex].getRootElementName();
	}


}
