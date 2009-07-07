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
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
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
public abstract class AbstractMultiRootChildDataModal extends
		DataAndFileContainer implements Runnable {

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

	private Shell parentShell;

	protected Shell shell;
	protected HelpGroup helpPanel;
	protected GenericButtonPanel buttonPanel;
	protected BaseNode selectedNode;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath Path from where the data will be read.
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public AbstractMultiRootChildDataModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(rootElementName, dataFilePath, configurationFilePath);
		this.parentShell = parentShell;
		this.selectedNode = selectedNode;
		this.shell = new Shell(parentShell, /* SWT.DIALOG_TRIM */SWT.BORDER
				| SWT.TITLE /* Parts of DIALOG_TRIM */| SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText(createCaption(selectedNode));
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

	protected abstract String createCaption(BaseNode selectedNode2);

	protected void open(){
		this.dataBindingContext = new DataBindingContext();
		buttonPanel = new GenericButtonPanel(this.shell);
		this.helpPanel = new HelpGroup((DataAndFileContainer)this, buttonPanel);
		((GenericButtonPanel) buttonPanel)
		.setModalParent((DataAndFileContainer) this);
	}

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

	public String getRootElementName() {
		return this.rootElementName;
	}

	public void setConfigurationFilePath(String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * Default implementation.
	 */
	public SideEffectProcessor getSavePreProcessor() {
		return null;
	}

	/**
	 * Default implementation.
	 */
	public SideEffectProcessor getSavePostProcessor() {
		return null;
	}
	public HelpGroup getHelpGroup(){
		return helpPanel;
	}
}
