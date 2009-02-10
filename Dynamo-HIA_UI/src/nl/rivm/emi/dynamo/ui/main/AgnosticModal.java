package nl.rivm.emi.dynamo.ui.main;

/**
 * BaseClass for Modal dialogs that are used to create and edit configuration 
 * files that are handled by an derivative of the AgnosticFactory. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

abstract public class AgnosticModal implements Runnable, DataAndFileContainer {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	protected Shell shell;
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	protected TypedHashMap modelObject;
	protected DataBindingContext dataBindingContext = null;
	protected String configurationFilePath;
	protected String rootElementName;
	protected HelpGroup helpPanel;
	protected BaseNode selectedNode;

	public AgnosticModal(Shell parentShell, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		this.configurationFilePath = configurationFilePath;
		this.rootElementName = rootElementName;
		this.selectedNode = selectedNode;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		shell.setText(createCaption(selectedNode));
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	/**
	 * Set the text in the titlebar above the modal window.
	 * @param selectedNode2
	 * @return
	 */
	abstract protected String createCaption(BaseNode selectedNode2);

	/**
	 * Common open behaviour for all supported windows.
	 */
	public synchronized void open() {
		try {
			dataBindingContext = new DataBindingContext();
			modelObject = manufactureModelObject();
			Composite buttonPanel = new GenericButtonPanel(shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			helpPanel = new HelpGroup(shell, buttonPanel);
			specializedOpenPart(buttonPanel);
			shell.pack();
			// This is the first place this works.
			shell.setSize(400, 400);
			shell.open();
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		} catch (DynamoInconsistentDataException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		}
	}

	/**
	 * The method name says it all, the Class that extends this baseclass must 
	 * implement its own special behaviour.
     *
	 * @param buttonPanel
	 */
	abstract protected void specializedOpenPart(Composite buttonPanel);

	protected TypedHashMap manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap producedData = null;
		AgnosticFactory factory = FactoryProvider
				.getRelevantFactoryByRootNodeName(rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: " + rootElementName);
		}
		File configurationFile = new File(configurationFilePath);
		if (configurationFile.exists()) {
			if (configurationFile.isFile() && configurationFile.canRead()) {
				producedData = factory.manufactureObservable(configurationFile);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				throw new ConfigurationException(configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			producedData = bootstrapModelObject(factory);
		}
		return producedData;
	}

	/**
	 * Method that creates a modelobject containing default LeafValue-s for 
	 * all ContainerValue-s(Age, Sex etc.) when no configuration file is supplied.
	 * 
	 * Contains behaviour that goes for the most simple ModelObjects. 
	 * 
	 * For instance: Objects that contain category layers must override this methods 
	 * to ensure the categories are initialized.
	 * @param factory
	 * @return
	 * @throws ConfigurationException
	 */
	protected TypedHashMap bootstrapModelObject(AgnosticFactory factory)
			throws ConfigurationException {
		TypedHashMap producedData = factory.manufactureObservableDefault();
		return producedData;
	}

	public void run() {
		open();
	}

	static protected void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	public Object getData() {
		return modelObject;
	}

	public String getFilePath() {
		return configurationFilePath;
	}

	public Object getRootElementName() {
		return rootElementName;
	}
}
