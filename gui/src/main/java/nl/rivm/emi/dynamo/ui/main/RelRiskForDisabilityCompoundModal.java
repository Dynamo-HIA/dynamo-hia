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
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksCompoundGroup;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

public class RelRiskForDisabilityCompoundModal extends AbstractDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private TypedHashMap<?> modelObject;
	int numberOfCategories;
	int durationClassIndex;

	public RelRiskForDisabilityCompoundModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Odds Ratios for disability from compound riskfactor";
	}

	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = manufactureModelObject();
//			BaseNode riskSourceNode = null;
			log.debug("Now for RelativeRisksCompoundGroup");
			RelativeRisksCompoundGroup relRiskForDisabilityCompoundGroup = new RelativeRisksCompoundGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel, this.durationClassIndex, null);
			relRiskForDisabilityCompoundGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(600, ModalStatics.defaultHeight);
			this.shell.setSize(675, ModalStatics.defaultHeight);
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
		File dataFile = new File(this.dataFilePath);
		if (dataFile.exists()) {
			if (dataFile.isFile() && dataFile.canRead()) {
				// 20090929 Added.
				if (factory instanceof AgnosticCategoricalFactory) {
					int numberOfClasses = RiskFactorUtil
							.getNumberOfRiskFactorClasses((BaseNode) ((ChildNode) this.selectedNode)
									.getParent());
					((AgnosticCategoricalFactory) factory)
							.setNumberOfCategories(numberOfClasses);
				}
				// ~ 20090929
				producedData = factory.manufactureObservable(dataFile,
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
			numberOfCategories = RiskFactorUtil
					.getNumberOfRiskFactorClasses(this.selectedNode);
			log.debug("numberOfCategories: " + numberOfCategories);
			durationClassIndex = RiskFactorUtil
			.getDurationCategoryIndex(selectedNode);
			log.debug("durationClassIndex: " + durationClassIndex);
			((RelRiskForDisabilityCompoundFactory) factory)
					.setNumberOfCategories(numberOfCategories);
		((CategoricalFactory)factory).setNumberOfCategories(numberOfCategories);	
			producedData = factory.manufactureObservableDefault();
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
