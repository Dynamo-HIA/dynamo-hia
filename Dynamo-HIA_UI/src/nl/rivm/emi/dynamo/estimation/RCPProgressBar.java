package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class RCPProgressBar implements ProgressIndicatorInterface {

	Shell shell = null;
	ProgressBar bar = null;

	public RCPProgressBar(Shell parentShell, String message) {
		super();
		shell = new Shell(parentShell);
		shell.setText(message);
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		bar = new ProgressBar(shell, SWT.NULL);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);

		shell.open();
	}

	@Override
	public void dispose() {
		shell.close();
		shell.dispose();
	}

	@Override
	public void setMaximum(int percent) {
		bar.setMaximum(percent);
	}

	@Override
	public void update(int percent) {
		bar.setSelection(percent);
	}

	@Override
	public int getPosition() {
		return bar.getSelection();
	}

}
