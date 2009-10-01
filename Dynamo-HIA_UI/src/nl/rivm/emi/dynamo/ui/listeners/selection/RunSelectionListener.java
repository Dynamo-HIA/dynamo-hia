package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class RunSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
	DataAndFileContainer modalParent;
	SaveSelectionListener mySaveSelectionListener;
	
	public RunSelectionListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
		this.mySaveSelectionListener = new SaveSelectionListener(
				modalParent);
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		// First save.
		mySaveSelectionListener.widgetSelected(arg0);
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
		} finally {
			control.setEnabled(true);
		}
	}

	private void runDynamoSimulation(String simName, String baseDir) {
		// Use the parentShell
		Shell parentShell = ((SimulationModal) modalParent).getParentShell();

		DynamoSimulationRunnable theSimulation = new DynamoSimulationRunnable(
				parentShell, simName, baseDir);
		theSimulation.run();
	}

}
