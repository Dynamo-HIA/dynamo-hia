package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.DynamoSimulationGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.RunButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class SimulationModal extends AbstractMultiRootChildDataModal {
	// @SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private DynamoSimulationObject modelObject;

	private boolean configurationFileExists;

	private DynamoSimulationGroup simulationGroup;

	private RunButtonPanel runButtonPanel = null;

	/**
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param configurationFileExists
	 */
	public SimulationModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, boolean configurationFileExists) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.configurationFileExists = configurationFileExists;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Simulation";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = new DynamoSimulationObject(manufactureModelObject());
		log.debug("modelObject" + modelObject);
		this.simulationGroup = new DynamoSimulationGroup(this.shell,
				this.modelObject, this.dataBindingContext, this.selectedNode,
				this.helpPanel, this, this.configurationFileExists);
		simulationGroup.setFormData(this.helpPanel.getGroup(), buttonPanel);
		this.shell.pack();
		// This is the first place this works.
		this.shell.setSize(ModalStatics.defaultSimulationWidth,
				ModalStatics.defaultHeight);
		this.shell.open();
	}

	@Override
	public Object getData() {
		return modelObject;
	}

	public void setRunButtonPanel(RunButtonPanel runButtonPanel) {
		if (this.runButtonPanel != null) {
			log.error("Suspicious behaviour: Setting runButtonPanel twice.");
		}
		this.runButtonPanel = runButtonPanel;
	}

	public RunButtonPanel getRunButtonPanel() {
		return runButtonPanel;
	}

	/**
	 * Signal the data handled by this Object has changed at least once.
	 */
	@Override
	public void setChanged(boolean changed) {
		// Callback to only enable the run-button when the configuration has
		// been saved.
		if (!isConfigurationFileReadOnly()) {
			if ((changed != this.changed) && (runButtonPanel != null)) {
				// 20091001 It is now a "Save and Run" button and can always be
				// enabled.
				// runButtonPanel.enableButton(!changed);
				runButtonPanel.enableButton(true);
			}
		} else {
			if (changed) {
				runButtonPanel.enableButton(false);
			}
		}
		super.setChanged(changed);
	}
}
