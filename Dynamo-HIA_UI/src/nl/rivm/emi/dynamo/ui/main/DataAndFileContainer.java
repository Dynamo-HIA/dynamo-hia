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
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Shell;

abstract public class DataAndFileContainer {
	/*
	 * The element name of the xml root
	 */
	protected String rootElementName;
	/*
	 * Path of the file that contains the data Must be "global" to be available
	 * to the save-listener.
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

	private boolean changed = false;

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
		if (configurationFilePath.equalsIgnoreCase(dataFilePath)) {
			File configurationFile = new File(configurationFilePath);
			if (configurationFile.exists()) {
				// Editing an existing file.
			} else {
				// New file.
				changed = true;
			}
		} else {
			// Imported file.
			changed = true;
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
		this.changed = changed;
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