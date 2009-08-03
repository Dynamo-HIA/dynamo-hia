package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * BaseClass for Modal dialogs that are used to create and edit configuration 
 * files that are handled by an derivative of the AgnosticFactory. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
abstract public class AgnosticModal extends AbstractDataModal {
	protected Log log = LogFactory.getLog(this.getClass().getName());

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
	public AgnosticModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	/**
	 * Common open behaviour for all supported windows.
	 */
	@Override
	public synchronized void open() {
		try {
			super.open();
			this.modelObject = manufactureModelObject();
			specializedOpenPart(buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(400, 400);
			this.shell.open();
			Display display = this.shell.getDisplay();
			while (!this.shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		} catch (DynamoInconsistentDataException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
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
	abstract protected void specializedOpenPart(Composite buttonPanel) throws ConfigurationException;

	@Override
	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory)FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: " + this.rootElementName);
		}
		File dataFile = new File(this.dataFilePath);
		if (dataFile.exists()) {
			if (dataFile.isFile() && dataFile.canRead()) {
				producedData = factory.manufactureObservable(dataFile, this.rootElementName);				
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				throw new ConfigurationException(this.configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			producedData = bootstrapModelObject(factory);
		}
		return producedData;
	}

	/**
	 * Method that creates a modelobject containing default LeafValue-s for all
	 * ContainerValue-s(Age, Sex etc.) when no configuration file is supplied.
	 * 
	 * Contains behaviour that goes for the most simple ModelObjects.
	 * 
	 * For instance: Objects that contain category layers must override this
	 * methods to ensure the categories are initialized.
	 * 
	 * @param factory
	 * @return
	 * @throws ConfigurationException
	 */
	protected TypedHashMap<?> bootstrapModelObject(AgnosticFactory factory)
			throws ConfigurationException {
		TypedHashMap<?> producedData = factory.manufactureObservableDefault();
		return producedData;
	}

}
