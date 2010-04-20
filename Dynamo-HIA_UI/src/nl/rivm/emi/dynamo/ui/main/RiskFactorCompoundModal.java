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

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalGroupFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCompoundObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCompoundGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardDirectoryStructureHandler;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RiskFactorCompoundModal extends AbstractMultiRootChildDataModal {

	// @SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorCompoundObject modelObject;
	/**
	 * When a new configuration is being edited this integer controls how many
	 * classes the riskfactor will have.
	 */
	int numberOfClasses;
	/**
	 * The index of the duration class that is now chosen beforehand and is
	 * fixed.
	 */
	int durationClassIndex;
	/**
	 * The TreeViewer this modal is displayed in.
	 */
	TreeViewer theViewer;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param numberOfCompoundClasses
	 *            TODO
	 * @param selectedDurationClassIndex
	 *            TODO
	 * @param theViewer
	 *            TODO
	 */
	public RiskFactorCompoundModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, int numberOfCompoundClasses,
			int selectedDurationClassIndex, TreeViewer theViewer) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		numberOfClasses = numberOfCompoundClasses;
		this.durationClassIndex = selectedDurationClassIndex;
		this.theViewer = theViewer;
	}

	public int getDurationClassIndex() {
		return durationClassIndex;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Compound risk factor configuration";
	}

	public synchronized void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = (RiskFactorCompoundObject) manufactureModelObject();
		// If this is a new configuration.
		if (durationClassIndex != -1) {
			this.modelObject.putDurationClass(durationClassIndex);
		}
		RiskFactorCompoundGroup riskFactorCategoricalGroup = new RiskFactorCompoundGroup(
				this.shell, this.modelObject, this.dataBindingContext,
				this.selectedNode, this.helpPanel);
		riskFactorCategoricalGroup.setFormData(this.helpPanel.getGroup(),
				buttonPanel);
		this.shell.pack();
		// This is the first place this works.
//		this.shell.setSize(400, ModalStatics.defaultHeight);
		this.shell.setSize(475, ModalStatics.defaultHeight);
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
				// 20091026 Test for location added, immediate import went
				// wrong.
				int numberOfClasses = -1;
				ParentNode parentNode = ((ChildNode) this.selectedNode)
						.getParent();
				if (!StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
						.equals(((BaseNode) parentNode).deriveNodeLabel())) {
					numberOfClasses = findNumberOfClasses(dataFile,
							(BaseNode) parentNode);
				} else {
					numberOfClasses = findNumberOfClasses(dataFile,
							selectedNode);
				}
				((AgnosticCategoricalGroupFactory) factory)
						.setNumberOfCategories(numberOfClasses);
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

	private int findNumberOfClasses(File dataFile, BaseNode parentNode)
			throws ConfigurationException, DynamoConfigurationException {
		int numberOfClasses;
		File configurationFile = new File(configurationFilePath);
		if (configurationFile.exists()) {
			numberOfClasses = RiskFactorUtil
					.getNumberOfRiskFactorClasses(parentNode);
		} else {
			// Immediate import.
			numberOfClasses = RiskFactorUtil
					.extractNumberOfCategories(dataFile);
		}
		return numberOfClasses;
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

	@Override
	/*
	 * Own implementation.
	 */
	public SideEffectProcessor getSavePostProcessor() {
		// return new SavePostProcessor();
		return null; // Caching makes the other solution impossible.
	}

	public class SavePostProcessor implements SideEffectProcessor {

		@SuppressWarnings("finally")
		synchronized public boolean doIt() {
			// New configuration-file about to be saved.
			boolean doSave = false;
			try {
				if (selectedNode instanceof DirectoryNode) {
					log.debug("SavePostProcessor is doing something");
					theViewer.refresh(); // Refresh the cache.
					StandardDirectoryStructureHandler
							.process((DirectoryNode) selectedNode);
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
