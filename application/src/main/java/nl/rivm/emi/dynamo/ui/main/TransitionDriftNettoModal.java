package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.TransitionDriftNettoGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class TransitionDriftNettoModal extends AbstractMultiRootChildDataModal{

	Log log = LogFactory.getLog(this.getClass().getName());

	TransitionDriftNettoObject nonGenericModelObject;

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
	public TransitionDriftNettoModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		// Decoupled rootElementName because of Garbage in, Garbage out.
		super(parentShell, dataFilePath, configurationFilePath,
				FileControlEnum.TRANSITIONDRIFT_NETTO.getRootElementName(), selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Transition Drift Netto";
	}

	@Override
	protected synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			nonGenericModelObject = new TransitionDriftNettoObject(manufactureModelObject());
			TransitionDriftNettoGroup transitionDriftNettoGroup = new TransitionDriftNettoGroup(
					this.shell, this.nonGenericModelObject,
					this.dataBindingContext, this.selectedNode, this.helpPanel);
			transitionDriftNettoGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(400, ModalStatics.defaultHeight);
			this.shell.setSize(475, ModalStatics.defaultHeight);
			this.shell.open();
	}

	public TransitionDriftNettoObject getData() {
		return nonGenericModelObject;
	}
}
