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
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.RelativeRisksCompoundGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.CompoundRiskFactorProperties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RelRiskFromRiskFactorCompoundModal extends AbstractDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private CompoundRiskFactorProperties props;

	private TypedHashMap<?> modelObject;
	int numberOfCategories;
	int durationClassIndex;

	public RelRiskFromRiskFactorCompoundModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode,
			CompoundRiskFactorProperties props) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.props = props;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risks for death from compound riskfactor";
	}

	@Override
	public synchronized void open() {
		try {
			if (props == null) {
				throw new DynamoInconsistentDataException(
						" RiskFactor properties have not been initialized.");
			}
			super.open();
			this.modelObject = manufactureModelObject();
			BaseNode riskSourceNode = null;
			if (this.props != null) {
				riskSourceNode = this.props.getRiskSourceNode();
			}
			log.debug("Now for RelativeRisksCompoundGroup");
			RelativeRisksCompoundGroup relRiskForDeathCompoundGroup = new RelativeRisksCompoundGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel, this.props.getDurationClassIndex(),
					riskSourceNode);
			relRiskForDeathCompoundGroup.setFormData(this.helpPanel.getGroup(),
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
			log.debug("Starting bootstrap construction.");
			numberOfCategories = props.getNumberOfCategories();
			log.debug("numberOfCategories: " + numberOfCategories);
			// durationClassIndex = RiskFactorUtil
			// .getDurationCategoryIndex(selectedNode);
			durationClassIndex = props.getDurationClassIndex();
			log.debug("durationClassIndex: " + durationClassIndex);
			((RelRiskFromRiskFactorCompoundFactory) factory)
					.setNumberOfCategories(numberOfCategories);
			// ((CategoricalFactory) factory)
			// .setNumberOfCategories(numberOfCategories);
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