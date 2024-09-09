package nl.rivm.emi.dynamo.ui.panels;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class NewbornsDialog extends Dialog {

	private String message;

	public NewbornsDialog(Shell parentShell, String message) {
		super(parentShell);
		this.message = message;
	}

	
    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        final Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            final Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        applyDialogFont(composite);
        return composite;
    }
}
