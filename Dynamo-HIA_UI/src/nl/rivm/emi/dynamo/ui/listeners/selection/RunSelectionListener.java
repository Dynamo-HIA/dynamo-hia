package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RunSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());

// 20091110 Can be less generic.	DataAndFileContainer modalParent;
	SimulationModal modalParent;
	SaveSelectionListener mySaveSelectionListener;

	public RunSelectionListener(SimulationModal modalParent) {
		this.modalParent = modalParent;
		// Listener to delegate saving to.
		this.mySaveSelectionListener = new SaveSelectionListener(modalParent);
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		// First save of not readonly.
		if(!modalParent.isConfigurationFileReadOnly()){
		mySaveSelectionListener.widgetSelected(arg0);
		}
		// Then run.
		Control control = ((Control) arg0.getSource());
		control.setEnabled(false);
		try {
			String filePath = modalParent.getConfigurationFilePath();
			String simulationDirectory = filePath.substring(0, filePath
					.lastIndexOf(File.separatorChar));
			String simulationName = simulationDirectory.substring(
					simulationDirectory.lastIndexOf(File.separatorChar) + 1,
					simulationDirectory.length());
			String baseDirectoryPath = simulationDirectory.substring(0,
					simulationDirectory.lastIndexOf(File.separator
							+ StandardTreeNodeLabelsEnum.SIMULATIONS
									.getNodeLabel() + File.separator));
			// Run the dynamo simulation model (the CDM)
			runDynamoSimulation(simulationName, baseDirectoryPath);
		} catch (Throwable t) {
			log.error("Running the Simulation threw a "
					+ t.getClass().getSimpleName() + " with message: "
					+ t.getMessage());
			displayErrorMessage(t);
		} finally {
			control.setEnabled(true);
		}
	}

	private void runDynamoSimulation(String simName, String baseDir)
			throws Throwable {
		// Use the parentShell
		Shell parentShell = ((SimulationModal) modalParent).getParentShell();
		Shell[] preRunChildren = parentShell.getShells();
		Set<Shell> preRunChildrenSet = new HashSet<Shell>();
		for (Shell preRunChild : preRunChildren) {
			preRunChildrenSet.add(preRunChild);
		}
		try {
			DynamoSimulationRunnable theSimulation = new DynamoSimulationRunnable(
					parentShell, simName, baseDir);
			theSimulation.run();
		} catch (Throwable t) {
			Shell[] postRunChildren = parentShell.getShells();
			for (Shell postRunChild : postRunChildren) {
				if (!preRunChildrenSet.contains(postRunChild)) {
					postRunChild.dispose();
				}
			}
			throw t;
		} finally {
			// Only cleanup after a Throwable, otherwise the result gets thrown
			// away...
			// Shell[] postRunChildren = parentShell.getShells();
			// for (Shell postRunChild : postRunChildren) {
			// if (!preRunChildrenSet.contains(postRunChild)) {
			// postRunChild.dispose();
			// }
			// }
		}
	}

	private void displayErrorMessage(Throwable e) {

		Shell shell = new Shell(modalParent.getParentShell());
		String cause = "";
		if (e.getCause() != null) {
			cause += this.handleErrorMessage("", e);
		}
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setMessage("Errors during configuration of the model"
				+ " Message given:\n" + e.getMessage() + cause);
		messageBox.open();
		e.printStackTrace();
	}

	private String handleErrorMessage(String cdmErrorMessage, Throwable e) {
		e.printStackTrace();
		// Show the error message and the nested cause of the error
		String errorMessage = "";
		if (e.getCause() != null) {
			if (!e.getCause().getMessage().contains(":")) {
				errorMessage = "An error occured: " + e.getMessage() + "\n"
						+ "Cause: " + e.getCause().getMessage();
			} else {
				errorMessage = "An error occured: " + e.getMessage() + "\n"
						+ "Cause: ";
				String[] splits = e.getCause().getMessage().split(":");
				for (int i = 1; i < splits.length; i++) {
					errorMessage += splits[i];
				}
			}
		} else {
			errorMessage = cdmErrorMessage;
		}
		this.log.error(errorMessage);
		return errorMessage;
	}

}
