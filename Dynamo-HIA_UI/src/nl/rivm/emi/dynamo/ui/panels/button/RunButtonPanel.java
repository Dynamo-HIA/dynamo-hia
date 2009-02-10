package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.selection.RunSelectionListener;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class RunButtonPanel extends Composite {

	Button runButton;
	DataAndFileContainer modalParent;

	public RunButtonPanel(Shell shell) {
		super(shell, SWT.NONE);
//		setSize(100, 35);
//		setFormData();
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		runButton = putRunButton(this);
		pack();
	}

	public void setFormData(Composite rightNeighbour, Composite bottomNeighbour) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(rightNeighbour, -5);
		formData.bottom = new FormAttachment(bottomNeighbour, -5);
		setLayoutData(formData);
	}

	public void setModalParent(DataAndFileContainer theParent) {
		modalParent = theParent;
		runButton.addSelectionListener(new RunSelectionListener(modalParent));
	}

	static private Button putRunButton(Composite panel) {
		FormData formData = new FormData();
		Button runButton = new Button(panel, SWT.PUSH);
		runButton.setText("Run");
		formData = new FormData();
		formData.left = new FormAttachment(0, 100);
		formData.bottom = new FormAttachment(100, -200);
		runButton.setLayoutData(formData);
		return runButton;
	}
}
