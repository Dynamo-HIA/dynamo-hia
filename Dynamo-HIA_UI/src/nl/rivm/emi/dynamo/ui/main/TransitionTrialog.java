package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Adds the Transition Panel to the InputBulletsTrialog modal.
 * 
 * The Transition Panel shows the risk factor name and type values.
 * (Risk Factor is the top panel!)
 * 
 * @author schutb
 * 
 */
public class TransitionTrialog extends InputBulletsTrialog {
	
	private static final String NAME = "Risk_Factor name:";
	private static final String TYPE = "Risk_Factor type:";
	
	private String riskFactorName;
	private String riskFactorType;

	private Control riskFactorArea;
	
	public TransitionTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator,
			String riskFactorName, String riskFactorType) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator,
				BulletButtonNamesEnum.ZERO, BulletButtonNamesEnum.USER_SPECIFIED, 
				BulletButtonNamesEnum.NETTO
				);
		this.riskFactorName = riskFactorName;
		this.riskFactorType = riskFactorType;
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
		this.riskFactorArea = createRiskFactorArea(composite);		
		this.dialogArea = createDialogArea(composite);
		this.bulletArea = createBulletArea(composite);
		this.buttonBar = createButtonBar(composite);

		return composite;
	}	

	protected Control createRiskFactorArea(Composite parent) {
		Composite composite = new Composite(parent, 0);

		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(7);
		layout.marginHeight = convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(7);
		layout.verticalSpacing = convertVerticalDLUsToPixels(7);
		composite.setLayout(layout);
		GridData data = new GridData(50);

		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		createNameAndTypeOfRiskFactorArea(composite);

		return composite;
	}


	private void createNameAndTypeOfRiskFactorArea(Composite composite) {
		createRiskFactorName(composite, this.riskFactorName);
		createRiskFactorType(composite, this.riskFactorType);		
	}

	private void createRiskFactorName(Composite composite,
			String riskFactorName) {
		((GridLayout) composite.getLayout()).numColumns += 1;
		Label riskFactorNameLabel = new Label(composite, SWT.NONE);
		riskFactorNameLabel.setText(NAME);		
		Label riskFactorNameValueLabel = new Label(composite, SWT.NONE);
		riskFactorNameValueLabel.setText(this.riskFactorName);
	}

	private void createRiskFactorType(Composite composite,
			String riskFactorType) {
		((GridLayout) composite.getLayout()).numColumns += 1;
		Label riskFactorTypeLabel = new Label(composite, SWT.NONE);
		riskFactorTypeLabel.setText(TYPE);
		Label riskFactorTypeValueLabel = new Label(composite, SWT.NONE);
		riskFactorTypeValueLabel.setText(this.riskFactorType);		
	}

	
	
}
