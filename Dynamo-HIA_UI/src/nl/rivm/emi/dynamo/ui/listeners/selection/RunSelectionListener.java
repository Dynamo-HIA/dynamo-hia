package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;

import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.simulation.DynamoSimulation;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.estimation.test.CoupledTestAll;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		String filePath = modalParent.getConfigurationFilePath();
		BaseNode selectedNode = ((SimulationModal)modalParent).getBaseNode();
		BaseNode currentNode = selectedNode;
		BaseNode parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
		
		String parentName = parentNode.deriveNodeLabel();
				
		while (!(parentNode instanceof RootNode)) {
			currentNode = parentNode;
			parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
		}
		String baseDirectoryPath = currentNode.getPhysicalStorage()
				.getAbsolutePath();
		@SuppressWarnings("unused")
		BaseDirectory baseDirectory = BaseDirectory.getInstance(baseDirectoryPath);
		
		// TODO Doesn't work when launched from the configuration file.
		String simulationName = parentName;
		File configurationFile = new File(filePath);
		// TODO: Integration replace with DynamoSimulationRunnable 
		
		// Run the dynamo simulation model (the CDM)
		runDynamoSimulation(simulationName, baseDirectoryPath);
				
		//CoupledTestAll test = new CoupledTestAll();
		/*
		try{
			// TODO: Integration replace with DynamoSimulationRunnable
			//test.entryPoint(baseDirectoryPath, simulationName);
		} catch(CDMRunException e){
			MessageBox messageBox = new MessageBox(((SimulationModal)modalParent).getShell(), SWT.ERROR_FAILED_EXEC);
			messageBox.setMessage("Simulation run threw a " + e.getClass().getName() 
					+ "\nwith message: " + e.getMessage());
			messageBox.open();
		}*/
	}

	private void runDynamoSimulation(String simName, String baseDir) {
		//Runnable theSimulation = null;
		// TODO: Use the parentShell in the Runnable and create the interface class: 
		Shell parentShell = ((SimulationModal)modalParent).getParentShell();
		//Shell shell = ((SimulationModal)modalParent).getShell();

		DynamoSimulationRunnable theSimulation = 
			new DynamoSimulationRunnable(parentShell, simName, baseDir);
		theSimulation.run();			
		
		// TODO: Closing/stopping?
		
		// Gebruik Subclass van Realm (geen Realm/databinding nodig) 
		//Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
			//	theSimulation);	

		
	}

}
