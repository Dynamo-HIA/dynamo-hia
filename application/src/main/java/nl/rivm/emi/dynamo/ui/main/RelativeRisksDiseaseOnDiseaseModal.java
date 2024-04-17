package nl.rivm.emi.dynamo.ui.main;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksMatrixAgeGroup;
import nl.rivm.emi.dynamo.ui.panels.TransitionMatrixAgeGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RelativeRisksUtil;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class RelativeRisksDiseaseOnDiseaseModal extends AbstractDataModal {

	/**
	 * Flag indicating the ModelObject in this class has been filled with
	 * standard values.
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
	public RelativeRisksDiseaseOnDiseaseModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Matrix of relative risks between diseases in a cluster.";
	}

	@Override
	protected void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = manufactureModelObject();
		RelativeRisksMatrixAgeGroup ageGroup = new RelativeRisksMatrixAgeGroup(
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
		} else {
			if (factory instanceof AgnosticCategoricalFactory) {
				File dataFile = new File(this.dataFilePath);
				if (dataFile.exists()) {
					// The configuration file with data already exists, fill the
					// modal
					// with existing data
					if (dataFile.isFile() && dataFile.canRead()) {
						int numberOfDiseases = RelativeRisksUtil.extractNumberOfDiseases(dataFile);
						((AgnosticCategoricalFactory) factory)
								.setNumberOfCategories(numberOfDiseases);
						producedData = factory.manufactureObservable(dataFile,
								this.rootElementName);
						if (producedData == null) {
							throw new ConfigurationException(
									"DataModel could not be constructed.");
						}
					}
				} else {
					// No file has been selected, continue without exceptions
					throw new ConfigurationException(this.dataFilePath
							+ " is no file or cannot be read.");
				}
			} else {
				// The configuration file with data does not yet exist, create a
				// new
				// screen object with default data
				int numberOfClasses = RiskFactorUtil
						.getNumberOfRiskFactorClasses(this.selectedNode);
				((CategoricalFactory) factory)
						.setNumberOfCategories(numberOfClasses);
				producedData = factory.manufactureObservableDefault();
				setHasDefaultObject(true);
			}
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
