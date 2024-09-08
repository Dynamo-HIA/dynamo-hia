package nl.rivm.emi.dynamo.ui.main.base;

/**
 * 
 * Exception handling OK
 * 
 */

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.DataAndFileContainer;
import nl.rivm.emi.dynamo.global.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.help.HelpTextManager;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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

	final protected Shell shell;
	protected HelpGroup helpPanel;
	protected GenericButtonPanel buttonPanel;
	protected BaseNode selectedNode;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param parentShell
	 *            Containing Shell
	 * @param dataFilePath
	 *            Path where the resulting configuration file is to be saved.
	 * @param configurationFilePath
	 *            Path to the imported configurationfile (if any).
	 * @param rootElementName
	 *            Name of the rootelement in the resulting configurationfile.
	 * @param selectedNode
	 *            Node on which the context menu was invoked to reach this
	 *            point.
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
		// 20100222 Added setLocation.
		this.shell.setLocation(25, 25);
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

	/**
	 * Creates the text that is put in the title bar of the modal window.
	 * 
	 * @param selectedNode2
	 * @return The text to put in the title bar.
	 */
	protected abstract String createCaption(BaseNode selectedNode2);

	/**
	 * Starts the opening process by creating a DataBindingContext and
	 * instantiating the Button- and Help-panels. After that the rest is
	 * delegated to levels lower in the inheritance tree.
	 * 
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	final protected void open() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.dataBindingContext = new DataBindingContext();
		buttonPanel = new GenericButtonPanel(this.shell);
		this.helpPanel = new HelpGroup((DataAndFileContainer) this, buttonPanel);
		HelpTextManager.initialize(helpPanel);
		((GenericButtonPanel) buttonPanel)
				.setModalParent((DataAndFileContainer) this);
		// 20090713 Added
		HelpTextManager.initialize(helpPanel);
		openModal();
	}

	/**
	 * Entrypoint for the delegation of the open() method.
	 * 
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	protected abstract void openModal() throws ConfigurationException,
			DynamoInconsistentDataException;

	/**
	 * This method constructs a model-object always containing Observables at
	 * the deepest level because these are needed for the databinding to work.
	 * 
	 * @return The manufactured modelobject.
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

	/**
	 * Starts the modal window.
	 * 
	 * Final forces the entrypoint to this level. (There were other entrypoints,
	 * that could cause unexpected behaviour.)
	 */
	final public void run() {
		if (ok2Run()) {
			try {
				open();
				if (!this.shell.isDisposed()) {
					Display display = this.shell.getDisplay();
					while (!this.shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}
				}
			} catch (ConfigurationException e) {
				MessageBox box = new MessageBox(this.shell,
						SWT.ERROR_UNSPECIFIED);
				box.setText("Processing " + this.configurationFilePath);
				box.setMessage(e.getMessage());
				box.open();
				this.shell.dispose();
			} catch (DynamoInconsistentDataException e) {
				MessageBox box = new MessageBox(this.shell,
						SWT.ERROR_UNSPECIFIED);
				box.setText("Processing " + this.configurationFilePath);
				box.setMessage(e.getMessage());
				box.open();
				this.shell.dispose();
			} catch (Throwable e) {
				e.printStackTrace(System.err);
				MessageBox box = new MessageBox(this.shell,
						SWT.ERROR_UNSPECIFIED);
				box.setText("Processing " + this.configurationFilePath);
				box.setMessage("An unexpected error occurred:\n"
						+ e.getClass().getSimpleName() + "\n" + e.getMessage()
						+ "\n" + dumpTopOfStackTrace(e));
				box.open();
				this.shell.dispose();
				// Will things be stable after this???????
			}
		}
	}

	/**
	 * Override to stop the screen from showing.
	 */
	protected boolean ok2Run() {
		return true;
	}

	/**
	 * Debugging helper method.<br/>
	 * Returns a String with the top three elements of the stacktrace contained
	 * in the Throwable passed.
	 * 
	 * @param thrown
	 *            Throwable of which the top of the stacktrace is deemed
	 *            interesting.
	 * @return The resulting stacktrace excerpt.
	 */
	private String dumpTopOfStackTrace(Throwable thrown) {
		final Integer topSize = 3;
		StringBuffer resultBuffer = new StringBuffer();
		StackTraceElement[] stackTraceElementArray = thrown.getStackTrace();
		for (int count = 0; (count < topSize)
				&& (count < stackTraceElementArray.length); count++) {
			resultBuffer.append(stackTraceElementArray[count].getClassName()
					+ "." + stackTraceElementArray[count].getMethodName() + "("
					+ stackTraceElementArray[count].getLineNumber() + ")\n");
		}
		return resultBuffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getData()
	 */
	abstract public Object getData();

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
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getBaseNode()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getConfigurationFilePath
	 * ()
	 */
	public String getConfigurationFilePath() {
		return this.configurationFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getDataFilePath()
	 */
	public String getDataFilePath() {
		return this.dataFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getRootElementName()
	 */
	public String getRootElementName() {
		return this.rootElementName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#setConfigurationFilePath
	 * (java.lang.String)
	 */
	public void setConfigurationFilePath(String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#setDataFilePath(java.
	 * lang.String)
	 */
	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * Default implementation, returns null.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getSavePreProcessor()
	 */
	public SideEffectProcessor getSavePreProcessor() {
		return null;
	}

	/**
	 * Default implementation, returns null.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getSavePostProcessor()
	 */
	public SideEffectProcessor getSavePostProcessor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.DataAndFileContainer#getHelpGroup()
	 */
	public HelpGroup getHelpGroup() {
		return helpPanel;
	}
}
