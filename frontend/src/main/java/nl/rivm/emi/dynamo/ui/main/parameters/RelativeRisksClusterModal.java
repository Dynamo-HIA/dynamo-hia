package nl.rivm.emi.dynamo.ui.main.parameters;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.parameters.RelativeRisksClusterAgeGroup;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class RelativeRisksClusterModal extends AbstractDataModal {

	/** Flag indicating the ModelObject in this class has been filled with standard values.
	 * 
	 */
	private boolean hasDefaultObject = false;

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
	public RelativeRisksClusterModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Transition Matrix";
	}

	@Override
	protected void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = manufactureModelObject();
		RelativeRisksClusterAgeGroup ageGroup = new RelativeRisksClusterAgeGroup(
				this.shell, SWT.NONE, selectedNode, modelObject,
				dataBindingContext, this.helpPanel, this.buttonPanel);
		// Refactoring to variable startup-size.
		Point leftMatrixPanelInitialSize = ageGroup.getInitialSize();
		int horizontalSize = leftMatrixPanelInitialSize.x * 2
				+ helpPanel.getGroup().getSize().x + 50;
		int verticalSize = ModalStatics.defaultHeight;
		this.shell.pack();
		// This is the first place this works.
		this.shell.setSize(horizontalSize, verticalSize);
		this.shell.open();
	}

	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		//
		if (((Object) factory) instanceof CategoricalFactory) {
			Index categoryIndex = (Index) XMLTagEntityEnum.INDEX.getTheType();
			int minIndex = categoryIndex.getMIN_VALUE();
			int maxIndex = categoryIndex.getMAX_VALUE();
			((CategoricalFactory) factory).setNumberOfCategories(maxIndex
					- minIndex + 1);
		}
		//
		File dataFile = new File(this.dataFilePath);

		if (dataFile.exists()) {
			// The configuration file with data already exists, fill the modal
			// with existing data
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
				// No file has been selected, continue without exceptions
				throw new ConfigurationException(this.dataFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			int numberOfClasses = RiskFactorUtil
					.getNumberOfRiskFactorClasses(this.selectedNode);
			((CategoricalFactory) factory)
					.setNumberOfCategories(numberOfClasses);
			producedData = factory.manufactureObservableDefault();
			setHasDefaultObject(true);
		}
		return producedData;
	}

	public void setHasDefaultObject(boolean hasDefaultObject) {
		this.hasDefaultObject = hasDefaultObject;
	}

	public boolean isHasDefaultObject() {
		return hasDefaultObject;
	}

}
