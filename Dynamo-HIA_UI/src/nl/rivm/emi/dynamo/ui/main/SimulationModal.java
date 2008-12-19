package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.PopulationSizeGroup;
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

public class SimulationModal implements Runnable, DataAndFileContainer {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private TypedHashMap lotsOfData;
	private DataBindingContext dataBindingContext = null;
	private String configurationFilePath;
	private String rootElementName;
	private HelpGroup helpPanel;
	private BaseNode selectedNode;

	public SimulationModal(Shell parentShell, String configurationFilePath,
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

	private String createCaption(BaseNode selectedNode2) {
		return "Simulation";
	}

	public synchronized void open() {
		try {
			dataBindingContext = new DataBindingContext();
			lotsOfData = manufactureModelObject();
			Composite buttonPanel = new GenericButtonPanel(shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			helpPanel = new HelpGroup(shell, buttonPanel);
			PopulationSizeGroup populationSizeGroup = new PopulationSizeGroup(
					shell, lotsOfData, dataBindingContext, selectedNode, helpPanel);
			populationSizeGroup.setFormData(helpPanel.getGroup(), buttonPanel);
			shell.pack();
			// This is the first place this works.
			shell.setSize(900, 700);
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

	private TypedHashMap manufactureModelObject()
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
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	public void run() {
		open();
	}

	static private void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	public Object getData() {
		return lotsOfData;
	}

	public String getFilePath() {
		return configurationFilePath;
	}

	public Object getRootElementName() {
		return rootElementName;
	}
}
