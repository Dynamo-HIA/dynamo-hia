package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.TransitionDriftNettoGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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
	protected synchronized void open() {
		try {
			super.open();
			nonGenericModelObject = new TransitionDriftNettoObject(manufactureModelObject());
			TransitionDriftNettoGroup transitionDriftNettoGroup = new TransitionDriftNettoGroup(
					this.shell, this.nonGenericModelObject,
					this.dataBindingContext, this.selectedNode, this.helpPanel);
			transitionDriftNettoGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(400, 250);
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

	public TransitionDriftNettoObject getData() {
		return nonGenericModelObject;
	}
}
