package nl.rivm.emi.dynamo.ui.main;

import java.util.Set;

import nl.rivm.emi.dynamo.ui.main.structure.BulletButtonNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
 *         20090518 mondeelr Opposite to the comment above, the number of radio
 *         buttons was fixed at three. It has now been refactored to enable a
 *         variable number of radio buttons.
 */

public class DropDownTrialog extends ImportExtendedInputTrialog {

	private BaseNode selectedNode;
	private String newFilePath;
	RiskSourcePropertiesMap selectableRiskSourcePropertiesMap;
	private RiskSourceProperties rsProps;
	private Text freePart;
	private Combo dropDown;
	private String newDataFilePath;
	private Control dropDownArea;

	public DropDownTrialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue,
			IInputValidator validator, BaseNode selectedNode) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.selectedNode = selectedNode;
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
		this.dropDownArea = createDropDownArea(composite);
		this.buttonBar = createButtonBar(composite);

		return composite;
	}

	protected Control createDropDownArea(Composite parent) {
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
		dropDown = new Combo(composite, SWT.DROP_DOWN);
		Set<String> keys = selectableRiskSourcePropertiesMap.keySet();
		for (String item : keys) {
			dropDown.add(item);
		}
		dropDown.select(0);
		return composite;
	}

	private void createTypeLabel(Composite composite) {
		((GridLayout) composite.getLayout()).numColumns += 1;
		Label riskFactorNameLabel = new Label(composite, SWT.NONE);
		riskFactorNameLabel.setText("Skblz.");
	}

}
