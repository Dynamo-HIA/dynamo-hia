package nl.rivm.emi.dynamo.ui.dialogs;

/**
 * 
 * Exception handling OK
 * 
 */

import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple input trialog for soliciting an input string from the user.
 * <p>
 * This concrete trialog class can be instantiated as is, or further subclassed
 * as required.
 * </p>
 */
public class DropDownAndImportExtendedInputTrialog extends
		ImportExtendedInputTrialog {
BaseNode selectedNode;
	RiskSourcePropertiesMap selectableRiskSourcePropertiesMap;
	private Combo dropDown;

	/**
	 * 
	 * Explicit constructor of this class
	 * 
	 * @param parentShell
	 * @param selectedNode
	 * @param dialogMessage
	 * @param initialValue
	 * @param validator
	 */
	public DropDownAndImportExtendedInputTrialog(Shell parentShell,
			BaseNode selectedNode, String dialogMessage, String initialValue,
			IInputValidator validator) {
		super(parentShell, "BasePath: " + selectedNode.getPhysicalStorage(),
				dialogMessage, initialValue, validator);
		this.selectedNode = selectedNode;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		try {
			selectableRiskSourcePropertiesMap = RiskSourcePropertiesMapFactory
					.make(selectedNode);
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// Dropdown
		if (true) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText("Dropdown test");
			GridData data = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		dropDown = new Combo(composite, SWT.DROP_DOWN);
		dropDown.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		dropDown.setBackground(new Color(null, 0x99, 0x99, 0x99));
		Set<String> keys = selectableRiskSourcePropertiesMap.keySet();
		for (String item : keys) {
			dropDown.add(item);
		}
		dropDown.select(0);
		// ~DropDown
		applyDialogFont(composite);
		return composite;
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
