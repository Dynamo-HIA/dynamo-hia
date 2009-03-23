package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.TransitionDriftNettoGroup;
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
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class TransitionDriftNettoModal extends AbstractDataModal {
	
	Log log = LogFactory.getLog(this.getClass().getName());
	
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
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Transition Drift Netto";
	}

	@Override
	protected synchronized void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			this.lotsOfData = manufactureModelObject();

			Composite buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this.shell, buttonPanel);
			TransitionDriftNettoGroup transitionDriftNettoGroup = new TransitionDriftNettoGroup(
					this.shell, this.lotsOfData, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			transitionDriftNettoGroup
					.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(900, 700);
			this.shell.open();
			Display display = this.shell.getDisplay();
			while (!this.shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}			
			
			/*
			// ALTERNATIVE CODE WITH THE TRIALOG
			// Call the input trialog modal here (trialog includes input field, import, ok and cancel buttons)
			ImportExtendedInputTrialog inputTrialog = new ImportExtendedInputTrialog(shell, 
					"Transition Drift Netto", "Trend", null, null);
			
			// TODO handle content
			
			// Set the value of the inputTrialog 
			// TODO Replace with lotsOfData field is same as setFormData
			inputTrialog.setValue("TREND"); 
			// Open the trialog
			int openValue = inputTrialog.open();
			
			log.debug("OpenValue is: " + openValue);
			
			// Returncode is the option selected on the trialog
			int returnCode = inputTrialog.getReturnCode();
	
			log.debug("ReturnCode is: " + returnCode);
			// OK or import has been selected
			if (returnCode != Window.CANCEL) {
				//TODO
				
				String trend = inputTrialog.getValue();
				// Import has been selected
				if (returnCode == ImportExtendedInputTrialog.IMPORT_ID) {
					//dataFile = this.getImportFile(); 
				} else {
					//???dataFile = savedFile;
				}
				
				// Create the xml file with the value				
			}
			*/
			
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
}
