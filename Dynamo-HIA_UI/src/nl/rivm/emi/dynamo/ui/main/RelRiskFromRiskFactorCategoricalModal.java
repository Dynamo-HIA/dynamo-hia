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
import java.util.Set;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RelRisksFromRiskFactorCategoricalGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;
import nl.rivm.emi.dynamo.ui.util.CategoricalRiskFactorProperties;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RelRiskFromRiskFactorCategoricalModal extends AbstractDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private CategoricalRiskFactorProperties props;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param props
	 */
	public RelRiskFromRiskFactorCategoricalModal(Shell parentShell,
			String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode,
			CategoricalRiskFactorProperties props) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.props = props;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risks from categorical riskfactor.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = manufactureModelObject();
		BaseNode riskSourceNode = null;
		if (this.props != null) {
			riskSourceNode = this.props.getRiskSourceNode();
		}
		RelRisksFromRiskFactorCategoricalGroup relRiskFromRiskFactorCategoricalGroup = new RelRisksFromRiskFactorCategoricalGroup(
				this.shell, this.modelObject, this.dataBindingContext,
				this.selectedNode, riskSourceNode, this.helpPanel);
		relRiskFromRiskFactorCategoricalGroup.setFormData(this.helpPanel
				.getGroup(), buttonPanel);
		this.shell.pack();
		// This is the first place this works.
		this.shell.setSize(500, ModalStatics.defaultHeight);
		this.shell.open();
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
		File dataFile = new File(this.dataFilePath);
		if (dataFile.exists()) {
			if (dataFile.isFile() && dataFile.canRead()) {
				// 20090929 Added.
				if (props == null) {
					RiskSourcePropertiesMap propsMap = null;
					String selectedNodeLabel = selectedNode.deriveNodeLabel();
					if(StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR.getNodeLabel().equals(selectedNodeLabel)){
					propsMap = RiskSourcePropertiesMapFactory
					.makeMap4OneRiskSourceType((BaseNode)selectedNode);
				} else {
					propsMap = RiskSourcePropertiesMapFactory
					.makeMap4OneRiskSourceType((BaseNode) ((ChildNode) selectedNode)
							.getParent());
				}
					// String selectedNodeLabel = selectedNode.deriveNodeLabel();
					Set<String> nameSet = propsMap.keySet();
					for (String riskSourceName : nameSet) {
						if ((selectedNodeLabel != null)
								&& !"".equals(selectedNodeLabel)) {
							int location = selectedNodeLabel
									.indexOf(riskSourceName);
							if ((location != -1)
									&& (location + riskSourceName.length() == selectedNodeLabel
											.length())) {
								props = (CategoricalRiskFactorProperties) propsMap
										.get(riskSourceName);
							break;
							}
						}
					}
				}
				((AgnosticCategoricalFactory) factory)
						.setNumberOfCategories(props.getNumberOfCategories());
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
			log.debug("props" + props);
			((RelRiskFromRiskFactorCategoricalFactory) factory)
					.setNumberOfCategories(props.getNumberOfCategories());
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

}
