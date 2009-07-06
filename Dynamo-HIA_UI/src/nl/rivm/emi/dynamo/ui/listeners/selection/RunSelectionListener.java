package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class RunSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
	DataAndFileContainer modalParent;

	public RunSelectionListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	// Upto V1.09_20090701
	// public void widgetSelected(SelectionEvent arg0) {
	// log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
	// + " got widgetSelected callback.");
	// String filePath = modalParent.getConfigurationFilePath();
	// BaseNode selectedNode = ((SimulationModal)modalParent).getBaseNode();
	// BaseNode currentNode = selectedNode;
	// BaseNode parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
	//		
	// String parentName = parentNode.deriveNodeLabel();
	//				
	// while (!(parentNode instanceof RootNode)) {
	// currentNode = parentNode;
	// parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
	// }
	// String baseDirectoryPath = currentNode.getPhysicalStorage()
	// .getAbsolutePath();
	// @SuppressWarnings("unused")
	// BaseDirectory baseDirectory =
	// BaseDirectory.getInstance(baseDirectoryPath);
	// String simulationName = parentName;
	// File configurationFile = new File(filePath);
	//		
	// // Run the dynamo simulation model (the CDM)
	// runDynamoSimulation(simulationName, baseDirectoryPath);
	// }

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		String filePath = modalParent.getConfigurationFilePath();
		String simulationDirectory = filePath.substring(0, filePath
				.lastIndexOf(File.separatorChar));
		String simulationName = simulationDirectory.substring(
				simulationDirectory.lastIndexOf(File.separatorChar)+1,
				simulationDirectory.length());
		String baseDirectoryPath = simulationDirectory.substring(0,
				simulationDirectory.lastIndexOf(File.separator
						+ StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
						+ File.separator));
		// Run the dynamo simulation model (the CDM)
		runDynamoSimulation(simulationName, baseDirectoryPath);
	}

	private void runDynamoSimulation(String simName, String baseDir) {
		// Use the parentShell
		Shell parentShell = ((SimulationModal) modalParent).getParentShell();

		DynamoSimulationRunnable theSimulation = new DynamoSimulationRunnable(
				parentShell, simName, baseDir);
		theSimulation.run();
	}

}
