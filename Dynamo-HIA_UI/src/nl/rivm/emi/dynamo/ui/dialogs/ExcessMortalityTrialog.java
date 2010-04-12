package nl.rivm.emi.dynamo.ui.dialogs;

import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.util.Log;

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
public class ExcessMortalityTrialog extends InputBulletsTrialog {

	private static final String NAME = "Disease name:";

	private String diseaseName;

	private Control diseaseArea;
	private String rootElementName;

	public ExcessMortalityTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue,
			IInputValidator validator, String diseaseName) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator,
				"Choose parameter type: ", new BulletButtonNamesEnum[] {
						BulletButtonNamesEnum.ACUTELY_FATAL,
						BulletButtonNamesEnum.CURED_FRACTION });
		this.diseaseName = diseaseName;
	}

	/**
	 * Temporary constructor to allow selective disabling (well, hiding) of
	 * radiobuttons.
	 * 
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogMessage
	 * @param initialValue
	 * @param validator
	 * @param diseaseName
	 * @param riskFactorType
	 * @param rootElementName
	 */
	public ExcessMortalityTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue,
			IInputValidator validator, String diseaseName,
			String rootElementName) {
		this(parentShell, dialogTitle, dialogMessage, initialValue, validator,
				diseaseName);
		this.rootElementName = rootElementName;
	}

	protected void initializeRadioButtons() {
		Log.debug("RootElementName is: " + rootElementName);
		radioButtons[0].setSelection(true);
		selectedBulletButtonIndex = 0; // Quick fix.
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(100));
		applyDialogFont(composite);

		initializeDialogUnits(composite);

		// Create the components of the bullet trialog
		this.diseaseArea = createDiseaseArea(composite);
		this.dialogArea = createDialogArea(composite);
		this.bulletArea = createBulletArea(composite);
		this.buttonBar = createButtonBar(composite);

		return composite;
	}

	protected Control createDiseaseArea(Composite parent) {
		Composite composite = new Composite(parent, 0);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(7);
		layout.marginHeight = convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(7);
		layout.verticalSpacing = convertVerticalDLUsToPixels(7);
		composite.setLayout(layout);
		GridData data = new GridData(50);

		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		createNameArea(composite);

		return composite;
	}

	private void createNameArea(Composite composite) {
		//((GridLayout) composite.getLayout()).numColumns += 1;
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText(NAME);
		Label nameValueLabel = new Label(composite, SWT.NONE);
		nameValueLabel.setText(this.diseaseName);
	}
}
