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
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCategoricalPrevalencesGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RiskFactorCategoricalPrevalencesModal extends AbstractDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

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
	public RiskFactorCategoricalPrevalencesModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,  
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Prevalences for a categorical riskfactor.";
	}

	/* (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = manufactureModelObject();
			RiskFactorCategoricalPrevalencesGroup riskFactorCategoricalPrevalencesGroup = new RiskFactorCategoricalPrevalencesGroup(
					this.shell, this.modelObject, this.dataBindingContext, this.selectedNode,
					this.helpPanel);
			riskFactorCategoricalPrevalencesGroup.setFormData(this.helpPanel
					.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(400, ModalStatics.defaultHeight);
			this.shell.setSize(475, ModalStatics.defaultHeight);
			this.shell.open();
	}

	@Override
	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory)FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File riskFactorCategoricalPrevalencesFile = new File(
				this.dataFilePath);
		if (riskFactorCategoricalPrevalencesFile.exists()) {
			if (riskFactorCategoricalPrevalencesFile.isFile()
					&& riskFactorCategoricalPrevalencesFile.canRead()) {
// 20090929 Added.
				int numberOfClasses = RiskFactorUtil
				.getNumberOfRiskFactorClasses((BaseNode) ((ChildNode)this.selectedNode).getParent());
				((AgnosticCategoricalFactory)factory).setNumberOfCategories(numberOfClasses);
// ~ 20090929				
				// toegevoegd door Hendriek in mei 2013
				if (numberOfClasses >18) throw new ConfigurationException("Risk factor data from risk factors"
						+ " with more than 18 classes can not be displayed or created.");
				producedData = factory
						.manufactureObservable(riskFactorCategoricalPrevalencesFile, this.rootElementName);
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
			int numberOfClasses = RiskFactorUtil
					.getNumberOfRiskFactorClasses(this.selectedNode);
			((RiskFactorPrevalencesCategoricalFactory) factory)
					.setNumberOfCategories(numberOfClasses);
			// toegevoegd door Hendriek in mei 2013
			if (numberOfClasses >18) throw new ConfigurationException("Risk factor data from risk factors"
					+ " with more than 18 classes can not be displayed or created.");
			
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}
}