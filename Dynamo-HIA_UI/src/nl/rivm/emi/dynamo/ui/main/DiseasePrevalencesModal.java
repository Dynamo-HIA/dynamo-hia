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
import nl.rivm.emi.dynamo.ui.panels.DiseasePrevalencesGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Modal screen for Disease Prevalence
 * 
 * @author schutb
 *
 */
public class DiseasePrevalencesModal extends AbstractDataModal {
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
	public DiseasePrevalencesModal(Shell parentShell, String dataFilePath,
			String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Disease prevalences";
	}

	/* (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			this.modelObject = manufactureModelObject();
			DiseasePrevalencesGroup diseasePrevalencesGroup = new DiseasePrevalencesGroup(
					this.shell, this.modelObject, this.dataBindingContext, this.selectedNode, this.helpPanel);
			diseasePrevalencesGroup.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(900, 700);
			this.shell.open();
	}
}
