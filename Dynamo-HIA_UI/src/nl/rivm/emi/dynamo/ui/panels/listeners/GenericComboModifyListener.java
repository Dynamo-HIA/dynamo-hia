package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.simulation.DiseaseTabDataManager;
import nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager;
import nl.rivm.emi.dynamo.ui.panels.simulation.GenericDropDownPanel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;

public class GenericComboModifyListener implements ModifyListener {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	/**
	 * The value in the model-object to update.
	 */
	private Set<GenericDropDownPanel> registeredDropDowns = 
		new HashSet<GenericDropDownPanel>();

	private DynamoTabDataManager dataManager;
	private GenericDropDownPanel dropDown;						
	
	public GenericComboModifyListener(GenericDropDownPanel dropDown) {
		super();
		this.dropDown = dropDown;
	}

	public void registerDropDown(GenericDropDownPanel dropdown) {
		registeredDropDowns.add(dropdown);
	}

	public void unRegisterDropDown(GenericDropDownPanel dropdown) {
		registeredDropDowns.remove(dropdown);
	}

	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();		
		
		log.debug("newText" + newText);		
		
		// First update the model
		try {
			this.dropDown.updateDataObjectModel(newText);
		} catch (ConfigurationException ce) {
			this.handleErrorMessage(ce, dropDown);
		}		
		// Iterate through the registered drop downs of this 
		log.debug("this.registeredDropDowns.size()" 
				+ this.registeredDropDowns.size());		
		// Update the registered (dependend) drop downs
		for (GenericDropDownPanel registeredDropDown : this.registeredDropDowns) {
			log.debug("registeredCombo" + registeredDropDown);						 
			try {				
				registeredDropDown.update(newText);			
			} catch (ConfigurationException ce) {
				this.handleErrorMessage(ce, registeredDropDown);
			}
		}
	}

	private void handleErrorMessage(Exception e, GenericDropDownPanel registeredDropDown) {
		this.log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(registeredDropDown.parent.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during update of the drop down " + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
	
}
