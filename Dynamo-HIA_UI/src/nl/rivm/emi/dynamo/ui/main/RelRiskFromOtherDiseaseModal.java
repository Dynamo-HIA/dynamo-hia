package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */


/**
 * Modal dialog to create and edit the population size XML files. 
 */
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.RelRisksFromOtherDiseaseGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.DiseaseProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

public class RelRiskFromOtherDiseaseModal extends AbstractDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	private RiskSourceProperties props;
	
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
	public RelRiskFromOtherDiseaseModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, DiseaseProperties props) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.props = props;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risks from other disease.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = manufactureModelObject();
			BaseNode riskSourceNode = null;
			if (this.props != null) {
				riskSourceNode = this.props.getRiskSourceNode();
			}
			RelRisksFromOtherDiseaseGroup relRiskFromOtherDiseaseGroup = new RelRisksFromOtherDiseaseGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, riskSourceNode, this.helpPanel);
			relRiskFromOtherDiseaseGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(500, ModalStatics.defaultHeight);
			this.shell.setSize(575, ModalStatics.defaultHeight);
			this.shell.open();
	}
}
