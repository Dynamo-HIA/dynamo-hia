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

import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalGroupFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.SideEffectProcessor;
import nl.rivm.emi.dynamo.global.StandardDirectoryStructureHandler;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCategoricalGroup;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 *
 */
/**
 * @author mondeelr
 * 
 */
public class RiskFactorCategoricalModal extends AbstractMultiRootChildDataModal {
//	private static final String RISKFACTOR_CATEGORICAL = "riskfactor_categorical";

	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorCategoricalObject modelObject;
	/**
	 * When a new configuration is being edited this integer controls how many
	 * classes the riskfactor will have.
	 */
	int numberOfClasses;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param numberOfClasses
	 *            TODO
	 */
	public RiskFactorCategoricalModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, int numberOfClasses) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.numberOfClasses = numberOfClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#createCaption
	 * (nl.rivm.emi.dynamo.ui.treecontrol.BaseNode)
	 */
	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Categorical risk factor configuration";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = new RiskFactorCategoricalObject(
					manufactureModelObject());
			RiskFactorCategoricalGroup riskFactorCategoricalGroup = new RiskFactorCategoricalGroup(
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
				// 20090929 Added.
				int numberOfClasses = RiskFactorUtil
				.getNumberOfRiskFactorClasses((BaseNode) ((ChildNode)this.selectedNode).getParent());
				((AgnosticCategoricalGroupFactory)factory).setNumberOfCategories(numberOfClasses);
// ~ 20090929				
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
			factory.setIndexLimit(numberOfClasses);
			log.debug("Setting numberOfClasses: " + numberOfClasses);
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}
	@Override
	/*
	 * Own implementation.
	 */
	public SideEffectProcessor getSavePostProcessor() {
		return new SavePreProcessor();
	}

	public class SavePreProcessor implements SideEffectProcessor {

		@SuppressWarnings("finally")
		synchronized public boolean doIt() {
			// New configuration-file about to be saved.
			boolean doSave = false;
			try {
				if (selectedNode instanceof DirectoryNode) {
					StandardDirectoryStructureHandler.process((DirectoryNode)selectedNode);
					doSave = true;
				} else {
					// No processing, but saving is allowed.
					doSave = true;
				}
			} catch (Exception e) {
				log.error("Caught Exception " + e.getClass().getName()
						+ " with message: \"" + e.getMessage() + "\".");
			} finally {
				return doSave;
			}
		}
	}

}
