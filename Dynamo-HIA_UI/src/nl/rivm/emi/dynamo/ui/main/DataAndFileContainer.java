package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.RunButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Shell;

abstract public class DataAndFileContainer {
	/*
	 * The element name of the xml root
	 */
	protected String rootElementName;
	/*
	 * Path of the file that contains the data to be read.
	 */
	protected String dataFilePath;

	/*
	 * Path of the file where the data will be written to or has been written
	 * Note that in case of an Import action the dataFilePath and
	 * configurationFilePath differ, and in case of an Save action they are
	 * equal
	 * 
	 * Must be "global" to be available to the save-listener.
	 */
	protected String configurationFilePath;
	protected TypedHashMap<?> modelObject;
	protected DataBindingContext dataBindingContext = null;

	/**
	 * Flag that indicates whether any content of the modelobject has changed
	 * since the last save.
	 */
	protected boolean changed = false;
	/**
	 * Flag that is set when the configurationfile being processed is readonly.
	 * Consequences:
	 * -The "Import" button is disabled, since importing is no
	 * use.
	 * -The "Save" and "Save and close" buttons are disabled when this flag
	 * is true. 
	 * -The "Save and Run" button reverts to just Run. 
	 * -The box after
	 * hitting the "Close" button does not appear when content was changed.
	 */
	protected boolean configurationFileReadOnly = false;

	public DataAndFileContainer(String rootElementName, String dataFilePath,
			String configurationFilePath/*
										 * Not yet present, TypedHashMap<?>
										 * modelObject, DataBindingContext
										 * dataBindingContext, boolean changed
										 */) {
		super();
		this.rootElementName = rootElementName;
		this.dataFilePath = dataFilePath;
		this.configurationFilePath = configurationFilePath;
		/**
		 * Modal constructed with an imported file.
		 */
		File configurationFile = new File(configurationFilePath);
		if (configurationFilePath.equalsIgnoreCase(dataFilePath)) {
			if (configurationFile.exists()) {
				// Editing an existing file.
				setChanged(false);
			} else {
				// New file.
				setChanged(true);
			}
		} else {
			// Imported file.
			setChanged(true);
		}
		if (configurationFile.exists()&& !configurationFile.canWrite()) {
			configurationFileReadOnly = true;
		}
	}

	public void setConfigurationFilePath(String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public String getConfigurationFilePath() {
		return configurationFilePath;
	}

	public Object getData() {
		return modelObject;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public String getRootElementName() {
		return rootElementName;
	}

	/**
	 * Signal the data handled by this Object has changed at least once.
	 */
	public void setChanged(boolean changed) {
// 2009110 RLM runButtonPanel kicked down to where it belongs, the SimulationModal.
		// Callback to only enable the run-button when the configuration has
		// been saved.
//		if ((changed != this.changed) && (runButtonPanel != null)) {
			// 20091001 It is now a "Save and Run" button and can always be
			// enabled.
			// runButtonPanel.enableButton(!changed);
//			runButtonPanel.enableButton(true);
//		}
		this.changed = changed;
	}

	public boolean isConfigurationFileReadOnly() {
		return configurationFileReadOnly;
	}

	/**
	 * Query the flag that indicates the data handled by this Object has changed
	 * at least once.
	 */
	public boolean isChanged() {
		return this.changed;
	}

	/**
	 * Possible extra functionality at "save" time.
	 */
	abstract public SideEffectProcessor getSavePreProcessor();

	abstract public SideEffectProcessor getSavePostProcessor();

	/**
	 * Methods should not be at this level, refactor later.
	 * 
	 * @return
	 */
	abstract public Shell getShell();

	abstract public Shell getParentShell();

	abstract public BaseNode getBaseNode();

	/**
	 * Too high up the inheritance tree, but anyway. Used to link the buttons to
	 * the helpsystem.
	 */
	abstract public HelpGroup getHelpGroup();
}