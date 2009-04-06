package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract class with for the data model screen
 * 
 * Two paths are used: One for the import file containing the data
 * (dataFilePath) and one for the configured application file
 * (configurationFilePath)
 * 
 */
public abstract class AbstractMultiRootChildDataModal implements Runnable,
		DataAndFileContainer {

	/**
	 * Abstract class with for the data model screen
	 * 
	 * Two paths are used: One for the import file containing the data
	 * (dataFilePath) and one for the configured application file
	 * (configurationFilePath)
	 * 
	 */

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	/*
	 * The element name of the xml root
	 */
	protected String rootElementName;

	private Shell parentShell;

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
	protected Shell shell;
	protected HashMap<String, Object> lotsOfData;
	protected DataBindingContext dataBindingContext = null;
	protected HelpGroup helpPanel;
	protected BaseNode selectedNode;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public AbstractMultiRootChildDataModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		this.dataFilePath = dataFilePath;
		this.configurationFilePath = configurationFilePath;
		this.rootElementName = rootElementName;
		this.parentShell = parentShell;
		this.selectedNode = selectedNode;
		this.shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText(createCaption(selectedNode));
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

	protected abstract String createCaption(BaseNode selectedNode2);

	protected abstract void open();

	/**
	 * This method constructs a model-object always containing Observables at
	 * the deepest level because these are needed for the databinding to work.
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	protected LinkedHashMap<String, Object> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> producedData = null;
		AgnosticGroupFactory factory = (AgnosticGroupFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File dataFile = new File(this.dataFilePath);

		if (dataFile.exists()) {
			// The configuration file with data already exists, fill the modal
			// with existing data
			if (dataFile.isFile() && dataFile.canRead()) {
				producedData = factory.manufactureObservable(dataFile,
						this.rootElementName);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				// No file has been selected, continue without exceptions
				throw new ConfigurationException(this.dataFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		open();
	}

	abstract public Object getData();

	public Shell getShell() {
		return this.shell;
	}

	public BaseNode getBaseNode() {
		return this.selectedNode;
	}

	public Shell getParentShell() {
		return this.parentShell;
	}

	public String getConfigurationFilePath() {
		return this.configurationFilePath;
	}

	public String getDataFilePath() {
		return this.dataFilePath;
	}

	public Object getRootElementName() {
		return this.rootElementName;
	}

	public void setConfigurationFilePath(String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}
}
