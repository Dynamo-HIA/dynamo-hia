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

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.DurationDistributionGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

public class DurationDistributionModal extends AbstractDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private TypedHashMap<?> modelObject;
	int durationClassIndex;
	/**
	 * Flag indicating the ModelObject in this class has been filled with
	 * standard values.
	 * 
	 */
	private boolean hasDefaultObject = false;

	public boolean isHasDefaultObject() {
		return hasDefaultObject;
	}

	public void setHasDefaultObject(boolean hasDefaultObject) {
		this.hasDefaultObject = hasDefaultObject;
	}

	public DurationDistributionModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Duration distribution for a compound riskfactor";
	}

	@Override
	public synchronized void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = manufactureModelObject();
		log.debug("Now for DurationDistributionGroup");
		DurationDistributionGroup durationDistributionGroup = new DurationDistributionGroup(
				this.shell, this.modelObject, this.dataBindingContext,
				this.selectedNode, this.helpPanel, this.durationClassIndex);
		durationDistributionGroup.setFormData(this.helpPanel.getGroup(),
				buttonPanel);
		this.shell.pack();
		// This is the first place this works.
		this.shell.setSize(600, 400);
		this.shell.open();
	}

	@Override
	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		durationClassIndex = RiskFactorUtil
				.getDurationCategoryIndex(selectedNode);
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File configurationFile = new File(this.configurationFilePath);
		if (configurationFile.exists()) {
			if (configurationFile.isFile() && configurationFile.canRead()) {
				producedData = factory.manufactureObservable(configurationFile,
						this.rootElementName);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				throw new ConfigurationException(this.configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			durationClassIndex = RiskFactorUtil
					.getDurationCategoryIndex(selectedNode);
			log.debug("durationClassIndex: " + durationClassIndex);
			producedData = factory.manufactureObservableDefault();
			hasDefaultObject = true;
		}
		return producedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#getData()
	 */
	@Override
	public Object getData() {
		return modelObject;
	}

}
