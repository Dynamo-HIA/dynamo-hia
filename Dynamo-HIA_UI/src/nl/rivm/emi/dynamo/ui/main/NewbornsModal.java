package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.NewbornsGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


/**
 * @author schutb
 *
 */
public class NewbornsModal extends AbstractMultiRootChildDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	private NewbornsObject modelObject;
	
	/**
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public NewbornsModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, NewbornsObject modelObject) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.modelObject = modelObject;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Newborns";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			// If the modelObject != null, then the Update button has been used
			if (this.modelObject == null) {
				this.modelObject = new NewbornsObject(manufactureModelObject());	
			}	
			log.debug("lotsOfData" + modelObject);
			Composite buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this.shell, buttonPanel);
			NewbornsGroup newbornsGroup = new NewbornsGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel,
					this);
			newbornsGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(900, 700);
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
		if(factory instanceof CategoricalFactory){
			
		}
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
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);			
			Integer maxYear = new Integer(calendar.get(Calendar.YEAR) + 29);
			// Set the limit to 30 years from the current year
			factory.setIndexLimit(maxYear);
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}	
	
	@Override
	public Object getData() {
		return this.modelObject;
	}

}
