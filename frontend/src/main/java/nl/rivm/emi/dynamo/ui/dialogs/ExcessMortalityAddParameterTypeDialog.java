package nl.rivm.emi.dynamo.ui.dialogs;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
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
 * Adds the ExcessMortality Panel to the InputBulletsTrialog modal.
 * 
 * The Transition Panel shows the risk factor name and type values. (Risk Factor
 * is the top panel!)
 * 
 * @author mondeelr
 * 
 */
public class ExcessMortalityAddParameterTypeDialog extends Dialog {
	/**
	 * The title of the dialog.
	 */
	private String title;

	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;

	protected Control bulletArea;

	/** A variable number of radiobuttons. */
	protected Button[] radioButtons = new Button[2];

	/* Initialized separately because setSelection does not generate an event. */
	protected int selectedBulletButtonIndex;

	protected BulletButtonNamesEnum[] bulletButtonNamesEnums;

	public ExcessMortalityAddParameterTypeDialog(Shell parentShell,
			String dialogTitle, String dialogMessage) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.bulletButtonNamesEnums = bulletButtonNamesEnums;
		// this.radioButtons = new Button[bulletButtonNamesEnums.length];
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		this.bulletArea = createBulletArea(composite);
		applyDialogFont(composite);
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
		createRadioButtons(composite);
		return composite;
	}

	private void createRadioButtons(Composite composite) {
		int count = 0;
		for (String radioButtonName : ExcessMortalityObject.ParameterTypeHelperClass.PARAMETERTYPES) {
			createRadioButton(composite, count, radioButtonName);
			count++;
		}
		initializeRadioButtons();
	}

	protected void initializeRadioButtons() {
		radioButtonAdministration(0);
	}

	/**
	 * 
	 */
	private void radioButtonAdministration(int id) {
		for (int idCounter = 0; idCounter < radioButtons.length; idCounter++) {
			if (idCounter == id) {
				radioButtons[idCounter].setSelection(true);
				selectedBulletButtonIndex = idCounter; // Quick fix.
				ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName = radioButtons[idCounter]
						.getText();
			} else {
				radioButtons[idCounter].setSelection(false);
			}
		}
	}

	// id is the radio button number
	// label is "categorical"
	protected void createRadioButton(Composite parent, final int id,
			final String radioButtonName) {
		// What is the purpose ?????
		((GridLayout) parent.getLayout()).numColumns += 1;

		// The radio button idiom
		this.radioButtons[id] = new Button(parent, SWT.RADIO);
		this.radioButtons[id].setText(radioButtonName);
		this.radioButtons[id].addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					radioButtonAdministration(id);
				}
			}
		});
	}
}
