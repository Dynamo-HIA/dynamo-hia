package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCategoricalFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.main.base.AgnosticModal;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksCategoricalGroup;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RelRiskForDisabilityCategoricalModal extends AgnosticModal {
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
	public RelRiskForDisabilityCategoricalModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Odds Ratios for disability from categorical riskfactor";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel) throws ConfigurationException {
		RelativeRisksCategoricalGroup relRiskForDisabilityCategoricalGroup = new RelativeRisksCategoricalGroup(
				this.shell, this.modelObject, this.dataBindingContext,
				this.selectedNode, this.helpPanel);
		relRiskForDisabilityCategoricalGroup.setFormData(this.helpPanel
				.getGroup(), buttonPanel);
	}

	@Override
	protected TypedHashMap<?> bootstrapModelObject(AgnosticFactory factory)
			throws ConfigurationException {
		TypedHashMap<?> producedData = null;
		int numberOfClasses = RiskFactorUtil
				.getNumberOfRiskFactorClasses(this.selectedNode);
		// toegevoegd door Hendriek in mei 2013
		if (numberOfClasses >18) throw new ConfigurationException("Risk factor data from risk factors"
				+ " with more than 18 classes can not be displayed or created.");
		((RelRiskForDisabilityCategoricalFactory) factory)
				.setNumberOfCategories(numberOfClasses);
		producedData = factory.manufactureObservableDefault();
		return producedData;
	}

	/**
	 * The AgnosticModal layer handles the opening....
	 * Must be present to keep the compiler happy.
	 */
	@Override
	public void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
	}

}
