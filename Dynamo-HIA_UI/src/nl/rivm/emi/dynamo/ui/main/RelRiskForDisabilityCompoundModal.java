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
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksCompoundGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RelRiskForDisabilityCompoundModal extends AbstractDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private TypedHashMap modelObject;
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
		return "Relative risks for disability from compound riskfactor";
	}

	@Override
	public synchronized void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			this.modelObject = manufactureModelObject();
			Composite buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this.shell, buttonPanel);
			BaseNode riskSourceNode = null;
			log.debug("Now for RelativeRisksCompoundGroup");
			RelativeRisksCompoundGroup relRiskForDisabilityCompoundGroup = new RelativeRisksCompoundGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel, this.durationClassIndex);
			relRiskForDisabilityCompoundGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(600, 400);
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

	@Override
	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		durationClassIndex = RiskSourcePropertiesMapFactory
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
			numberOfCategories = RiskSourcePropertiesMapFactory
					.getNumberOfRiskFactorClasses(this.selectedNode);
			log.debug("numberOfCategories: " + numberOfCategories);
			durationClassIndex = RiskSourcePropertiesMapFactory
			.getDurationCategoryIndex(selectedNode);
			log.debug("durationClassIndex: " + durationClassIndex);
			((RelRiskForDisabilityCompoundFactory) factory)
					.setNumberOfCategories(numberOfCategories);
		((CategoricalFactory)factory).setNumberOfCategories(numberOfCategories);	
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	public void run() {
		open();
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