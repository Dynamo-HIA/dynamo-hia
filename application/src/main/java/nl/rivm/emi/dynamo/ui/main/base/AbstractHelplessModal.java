package nl.rivm.emi.dynamo.ui.main.base;

/**
 * 
 * Exception handling OK
 * 
 */

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class AbstractHelplessModal implements Runnable {

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
	final protected Shell shell;
	protected BaseNode selectedNode;
	String caption;

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
	public AbstractHelplessModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, String caption) {
		this.dataFilePath = dataFilePath;
		this.configurationFilePath = configurationFilePath;
		this.rootElementName = rootElementName;
		this.parentShell = parentShell;
		this.selectedNode = selectedNode;
		this.caption = caption;
		this.shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText(caption);
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

//	protected abstract String createCaption(BaseNode selectedNode2);

	protected abstract void open();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		open();
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
