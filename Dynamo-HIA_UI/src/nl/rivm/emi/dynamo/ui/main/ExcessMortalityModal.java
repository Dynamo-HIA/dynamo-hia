package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.ExcessMortalityGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 *
 */
public class ExcessMortalityModal extends AbstractMultiRootChildDataModal {
	private static final String EXCESSMORTALITY = "excess_mortality";
	
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private ExcessMortalityObject modelObject;

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
	public ExcessMortalityModal(Shell parentShell, String dataFilePath, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);		
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#createCaption(nl.rivm.emi.dynamo.ui.treecontrol.BaseNode)
	 */
	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Excess mortality";
	}

	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#open()
	 */
	@Override	
	public synchronized void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			this.modelObject = new ExcessMortalityObject(manufactureModelObject());

			Composite buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this.shell, buttonPanel);
			ExcessMortalityGroup excessMortalityGroup = new ExcessMortalityGroup(
					this.shell, this.modelObject, this.dataBindingContext, this.selectedNode, this.helpPanel);
			excessMortalityGroup.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(500, 500);
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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}
}