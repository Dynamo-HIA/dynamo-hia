package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.dynamo.output.ErrorMessageWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GraphicalDynSimRunPR implements DynSimRunPRInterface {

	Shell parentShell = null;

	public GraphicalDynSimRunPR(Shell parentShell) {
		super();
		this.parentShell = parentShell;
	}

	@Override
	public Shell getShell() {
		return parentShell;
	}

	@Override
	public void communicateErrorMessage(DynamoSimulationRunnable dynSimRun, Exception e, String simulationFilePath) {
		Shell shell = new Shell(parentShell);
		String cause = "";
		if (e.getCause() != null) {
			cause += dynSimRun.handleErrorMessage("", e, simulationFilePath);
		}
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setMessage("Errors during configuration of the model"
				+ " Message given: " + e.getMessage() + cause);
		messageBox.open();
	}

	@Override
	public void usedToBeErrorMessageWindow(Exception e) {
		new ErrorMessageWindow(e, parentShell);
	}

	@Override
	public void usedToBeErrorMessageWindow(String string) {
		new ErrorMessageWindow(string, parentShell);
	}

	@Override
	public ProgressIndicatorInterface createProgressIndicator(String message) {
		ProgressIndicatorInterface instance = new RCPProgressBar(parentShell, message); 
		return instance;
	}
}