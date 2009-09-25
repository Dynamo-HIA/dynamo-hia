package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */

import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousPrevalencesObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorContinuousPrevalencesGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author mondeelr
 *
 */
public class RiskFactorContinuousPrevalencesModal extends AbstractMultiRootChildDataModal {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorContinuousPrevalencesObject modelObject;

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
	public RiskFactorContinuousPrevalencesModal(Shell parentShell, String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);		
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#createCaption(nl.rivm.emi.dynamo.ui.treecontrol.BaseNode)
	 */
	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Prevalences for a Continuous Risk Factor";
	}

	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#open()
	 */
	@Override	
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = (RiskFactorContinuousPrevalencesObject)manufactureModelObject();
			RiskFactorContinuousPrevalencesGroup riskFactorContinuousPrevalencesGroup = new RiskFactorContinuousPrevalencesGroup(
					this.shell, this.modelObject, this.dataBindingContext, this.selectedNode, this.helpPanel);
			riskFactorContinuousPrevalencesGroup.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(600, 500);
			this.shell.open();
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}
}
