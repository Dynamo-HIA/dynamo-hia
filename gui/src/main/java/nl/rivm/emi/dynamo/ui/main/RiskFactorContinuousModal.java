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
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorContinuousGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RiskFactorContinuousModal extends AbstractMultiRootChildDataModal {
//	private static final String RISKFACTOR_CONTINUOUS = "riskfactor_continuous";

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorContinuousObject modelObject;
	private int numberOfCutoffs;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param selectedNumberOfCutoffs
	 *            TODO
	 */
	public RiskFactorContinuousModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, int selectedNumberOfCutoffs) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		numberOfCutoffs = selectedNumberOfCutoffs;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Continuous risk factor configuration";
	}

	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = (RiskFactorContinuousObject) manufactureModelObject();
			RiskFactorContinuousGroup riskFactorCategoricalGroup = new RiskFactorContinuousGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			riskFactorCategoricalGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(500, ModalStatics.defaultHeight);
			this.shell.setSize(575, ModalStatics.defaultHeight);
			this.shell.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}

	/**
	 * This method constructs a model-object always containing Observables at
	 * the deepest level because these are needed for the databinding to work.
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	@Override
	protected LinkedHashMap<String, Object> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> producedData = null;
		AgnosticGroupFactory factory = (AgnosticGroupFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (!(factory instanceof CategoricalFactory)) {
			throw new ConfigurationException("Factory for rootElementName: "
					+ this.rootElementName
					+ " should implement CategoricalFactory.");
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
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			((CategoricalFactory) factory)
					.setNumberOfCategories(numberOfCutoffs);
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

}
