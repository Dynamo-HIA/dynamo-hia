package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
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
	private Set<GenericDropDownPanel> registeredDropDowns = new HashSet<GenericDropDownPanel>();

	private DynamoTabDataManager dataManager;
	private GenericDropDownPanel dropDown;
	private HelpGroup helpGroup;

	public GenericComboModifyListener(GenericDropDownPanel dropDown,
			HelpGroup helpGroup) {
		super();
		this.dropDown = dropDown;
		this.helpGroup = helpGroup;
	}

	public void registerDropDown(GenericDropDownPanel dropdown) {
		registeredDropDowns.add(dropdown);
	}

	public void unRegisterDropDown(GenericDropDownPanel dropdown) {
		registeredDropDowns.remove(dropdown);
	}

	public void modifyText(ModifyEvent event) {
		helpGroup.getTheModal().setChanged(true);
		Combo myCombo = (Combo) event.widget;
		eventlessModifyText(myCombo);
	}

	/**
	 * Method extracted to allow the reuse of the functionality without Events.
	 * 
	 * @param myCombo
	 */
	public void eventlessModifyText(Combo myCombo) {
		String newText = myCombo.getText();

		log.debug("newText in listeber" + newText);

		// First update the model
		/* hendriek : toegevoegd: synchronized */
		try {
			synchronized (this.dropDown) {
				this.dropDown.updateDataObjectModel(newText);
				if (newText.toLowerCase().contains("proxy")) this.dropDown.setWarningColor();
				else this.dropDown.disableWarningColor();
			}
		} catch (ConfigurationException ce) {
			this.handleErrorMessage(ce, dropDown);
		} catch (NoMoreDataException e) {
			this.handleErrorMessage(e, dropDown);
			e.printStackTrace();
		} catch (DynamoNoValidDataException e) {
			this.handleErrorMessage(e, dropDown);
			e.printStackTrace();
			
		}
		// Iterate through the registered drop downs of this
		log.debug("this.registeredDropDowns.size()"
				+ this.registeredDropDowns.size());
		// Update the registered (dependend) drop downs
		// does not work as now the model state is no longer updated when changes are made
		// first remove the listerners to prevent that they trigger eachother
	//	for (GenericDropDownPanel registeredDropDown : this.registeredDropDowns) {
	//		registeredDropDown.getDropDown().removeModifyListener(registeredDropDown.getGenericComboModifyListener());
	//	}
		for (GenericDropDownPanel registeredDropDown : this.registeredDropDowns) {
			log.debug("registeredCombo" + registeredDropDown);
			try {
				registeredDropDown.update(newText);
			} catch (ConfigurationException ce) {
				this.handleErrorMessage(ce, registeredDropDown);
			} catch (NoMoreDataException e) {
				this.handleErrorMessage(e, registeredDropDown);

			} catch (DynamoNoValidDataException e) {

				this.handleErrorMessage(e, registeredDropDown);
			}
			
			
		} // and add the listeners again
	//	for (GenericDropDownPanel registeredDropDown : this.registeredDropDowns) {
	//		registeredDropDown.getDropDown().addModifyListener(registeredDropDown.getGenericComboModifyListener());
	//	}
	}

	private void handleErrorMessage(Exception e,
			GenericDropDownPanel registeredDropDown) {
		this.log.debug(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(registeredDropDown.parent.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during update of the drop down "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

}
