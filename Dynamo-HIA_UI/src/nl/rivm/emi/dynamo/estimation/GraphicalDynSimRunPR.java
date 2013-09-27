package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.ErrorMessageWindow;
import nl.rivm.emi.dynamo.ui.panels.output.Output_UI;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GraphicalDynSimRunPR implements DynSimRunPRInterface {

	Shell parentShell = null;
	ProgressIndicatorInterface bar=null;

	public GraphicalDynSimRunPR(Shell parentShell) {
		super();
		this.parentShell = parentShell;
	}

	@Override
	public void communicateErrorMessage(DynamoSimulationRunnable dynSimRun,
			Throwable e, String simulationFilePath) {
		Shell shell = new Shell(parentShell);
		String cause = "";
		if (e.getCause() != null) {
			cause += dynSimRun.handleErrorMessage("", (Exception) e, simulationFilePath);
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
		bar = new RCPProgressBar(parentShell,
				message);
		return bar;
	//	while (!parentShell.isDisposed ()) {
	//		if (!parentShell.getDisplay().readAndDispatch ()) parentShell.getDisplay().sleep ();
	//	}

		
	}

	
	@Override
	public ProgressIndicatorInterface createProgressIndicator(String message,
			Boolean indeterminate) {bar = new RCPProgressBar(parentShell,
					message,indeterminate);
			return bar;
	}
	public void dispatchProgressBar(){
		  this.bar.dispose();
	}


	


	@Override
	public void createOutput(CDMOutputFactory output,
			ScenarioParameters scenarioParameters, String currentPath) {
		if (parentShell != null) {
			new Output_UI(parentShell, output, scenarioParameters, currentPath);
		}
	}


	@Override
	public void updateProgressIndicator() {
		Display display=this.parentShell.getDisplay();
		if (display.isDisposed()) return;
		display.asyncExec(new Runnable() {
			public void run() {
			if (bar.isDisposed ()) return;
			 int newValue=bar.getSelection()+1;
				bar.setSelection(newValue);
			}
		});
		// TODO Auto-generated method stub
		
	}
	public RCPProgressBar getBar(){
		return (RCPProgressBar) this.bar;
	}

	@Override
	public Display getDisplay() {
		return	this.parentShell.getDisplay();
		 
	}

	

}
