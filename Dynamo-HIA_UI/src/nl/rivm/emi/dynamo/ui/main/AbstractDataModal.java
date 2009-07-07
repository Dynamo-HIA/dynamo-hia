package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.listeners.shell.MyShellListener;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
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
 * Two paths are used:
 * One for the import file containing the data (dataFilePath) and 
 * one for the configured application file (configurationFilePath)
 * 
 */	
public abstract class AbstractDataModal extends DataAndFileContainer implements Runnable{
	
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
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public AbstractDataModal(Shell parentShell, String dataFilePath, 
			String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(rootElementName, dataFilePath, configurationFilePath);
		this.parentShell = parentShell;
		this.selectedNode = selectedNode;
		this.shell = new Shell(parentShell, /* SWT.DIALOG_TRIM */ SWT.BORDER | SWT.TITLE /* Parts of DIALOG_TRIM */| SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText(createCaption(selectedNode));
		this.shell.addShellListener(new MyShellListener());
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

	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory)FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: " + this.rootElementName);
		}
		//
		if(((Object)factory) instanceof CategoricalFactory){
			Index categoryIndex = (Index) XMLTagEntityEnum.INDEX.getTheType();
			int minIndex = categoryIndex.getMIN_VALUE();
			int maxIndex = categoryIndex.getMAX_VALUE();
			((CategoricalFactory)factory).setNumberOfCategories(maxIndex - minIndex + 1);
		}
		//
		File dataFile = new File(this.dataFilePath);
		
		if (dataFile.exists()) {
			// The configuration file with data already exists, fill the modal with existing data
			if (dataFile.isFile() && dataFile.canRead()) {
				producedData = factory.manufactureObservable(dataFile, this.rootElementName);
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
			// The configuration file with data does not yet exist, create a new screen object with default data
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		open();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#getData()
	 */
	public Object getData() {
		return this.modelObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getShell()
	 */
	public Shell getShell() {
		return this.shell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#getBaseNode()
	 */
	public BaseNode getBaseNode() {
		return this.selectedNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getParentShell()
	 */
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
