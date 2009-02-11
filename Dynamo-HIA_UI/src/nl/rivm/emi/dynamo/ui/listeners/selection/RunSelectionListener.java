package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.objects.layers.StaxWriterEntryPoint;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.test.CoupledTestAll;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

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
		String filePath = modalParent.getFilePath();
		BaseNode selectedNode = ((SimulationModal)modalParent).getSelectedNode();
		BaseNode currentNode = selectedNode;
		BaseNode parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
		while (!(parentNode instanceof RootNode)) {
			currentNode = parentNode;
			parentNode = (BaseNode) ((ChildNode) currentNode).getParent();
		}
		String baseDirectoryPath = currentNode.getPhysicalStorage()
				.getAbsolutePath();
		@SuppressWarnings("unused")
		BaseDirectory baseDirectory = BaseDirectory.getInstance(baseDirectoryPath);
		// TODO Doesn't work when launched from the configuration file.
		String simulationName = selectedNode.deriveNodeLabel();
		File configurationFile = new File(filePath);
		CoupledTestAll test = new CoupledTestAll();
		try{
		test.entryPoint(baseDirectoryPath, simulationName);
		} catch(CDMRunException e){
			MessageBox messageBox = new MessageBox(((SimulationModal)modalParent).getShell(), SWT.ERROR_FAILED_EXEC);
			messageBox.setMessage("Simulation run threw a " + e.getClass().getName() 
					+ "\nwith message: " + e.getMessage());
			messageBox.open();
		}
	}

}
