package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.RelativeRiskComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * The generic drop down present in all tabs
 * 
 * @author schutb
 * 
 */
public class RelativeRiskDropDownPanel {

	/**
	 * Possible labels for this dropdownpanel.
	 */
	public static final String FROM = "From";
	public static final String TO = "To";
	public static final String RELATIVE_RISK = "Relative Risk";

	private Log log = LogFactory.getLog(this.getClass().getName());

	public Composite parent;
	private Combo dropDown;
	private DropDownPropertiesSet selectablePropertiesSet;
	private RelativeRiskTabDataManager myDataManager;
	private HelpGroup helpGroup;
	/**
	 * Actual label for this instance.
	 */
	private String dropDownLabel;
	RelativeRiskComboModifyListener relativeRiskComboModifyListener;

	public RelativeRiskDropDownPanel(Composite parent, String dropDownLabel,
			int columnSpan, RelativeRiskTabDataManager myDataManager,
			HelpGroup helpGroup, DropDownPropertiesSet selections)
			throws ConfigurationException {
		this.parent = parent;
		this.dropDownLabel = dropDownLabel;
		this.myDataManager = myDataManager;
		this.helpGroup = helpGroup;
		this.selectablePropertiesSet = selections;
		Label label = new Label(parent, SWT.LEFT);
		label.setText(dropDownLabel + ":");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(layoutData);
		dropDown = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		myDataManager.addCombo2Lookups(dropDown, dropDownLabel);
		if (selections != null) {
			dropDown.setItems(selections.toArray());
		}
		GridData dropLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		dropLayoutData.horizontalSpan = columnSpan;
		// dropLayoutData.marginHeight = 0;
		dropDown.setLayoutData(dropLayoutData);
		// TODO Maybe reactivate this.fill(selectablePropertiesSet);
		this.relativeRiskComboModifyListener = myDataManager
				.getRelativeRiskComboModifyListener();
		dropDown.addModifyListener(relativeRiskComboModifyListener);
		//
//		Event event = new Event();
//		event.widget = dropDown;
//		event.text = null;
//		ModifyEvent modifyEvent = new ModifyEvent(event);
//		relativeRiskComboModifyListener.modifyText(modifyEvent);
	}

	private void setDefaultValue() throws ConfigurationException {
		// Get the default value
		String currentValue = myDataManager.getCurrentValue(this.getLabel());
		log.debug("CURRENTVALUEDEF: " + currentValue);
		log.debug("getCurrentIndex(currentValue)"
				+ getCurrentIndex(currentValue));

		// Retrieve the index value
		int index = getCurrentIndex(currentValue);

		// Set the default value
		dropDown.select(index);
		if (currentValue != null)
			myDataManager.setDefaultValue(this.getLabel(), currentValue);
	}

	/**
	 * select the configured value
	 * 
	 * @param configuredValue
	 * @throws ConfigurationException
	 */
	public void selectConfiguredValue(String configuredValue)
			throws ConfigurationException {
		int index = getCurrentIndex(configuredValue);
		log.debug("PropertiesSet: " + selectablePropertiesSet);
		log.debug("configuredValue: " + configuredValue + " found at index: "
				+ index);
		dropDown.select(index);
	}

	private int getCurrentIndex(String currentValue) {
		int result = 0;
		if ((currentValue != null) && (selectablePropertiesSet != null)) {
			result = selectablePropertiesSet.getSelectedIndex(currentValue);
		}
		return result;
	}

	public void fill(DropDownPropertiesSet set) {
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}

	public void update(String newText) throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		updateRegisteredDropDown(newText);
	}

	/**
	 * 
	 * Update the registered property sets after selection
	 * 
	 * @param newText
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 * @throws DynamoNoValidDataException
	 */
	private void updateRegisteredDropDown(String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		dropDown.removeAll();
		log.debug("newText" + newText);
		log.debug("this.getLabel()" + this.getLabel());
		selectablePropertiesSet.clear();
		selectablePropertiesSet.addAll(myDataManager.getDropDownSet(this
				.getLabel(), newText));
		log.debug("SET" + this.selectablePropertiesSet);
		// TODO fill(this.selectablePropertiesSet);
		dropDown.select(0);
	}

	/**
	 * @deprecated Refresh list after tab change, only for the selection group
	 * 
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 * @throws DynamoNoValidDataException
	 */
	public void refresh() throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		log.debug("REFRESH");
		dropDown.removeModifyListener(this.relativeRiskComboModifyListener);
		dropDown.removeAll();
		selectablePropertiesSet.clear();
		selectablePropertiesSet.addAll(myDataManager
				.getRefreshedDropDownSet(this.getLabel()));
		log.debug("SET" + this.selectablePropertiesSet);
		fill(selectablePropertiesSet);
		// Remove old value (is choosable again)
		// myDataManager.removeOldDefaultValue(this.getLabel());
		// Set the new default (can be the same value as the removed one)
		setDefaultValue();
		dropDown.addModifyListener(relativeRiskComboModifyListener);
		// TODO: fire an event for the modify listener to update the dependend
		// drop downs
	}

	/**
	 * @deprecated
	 */
	public void updateDataObjectModel(String newText)
			throws ConfigurationException, NoMoreDataException {
		// Remove old value (is choosable again)
		// myDataManager.removeOldDefaultValue(this.getLabel());
		// Add new value
		myDataManager.updateObjectState(this.getLabel(), newText);
	}

	public String getLabel() {
		return this.dropDownLabel;
	}

	// public RelativeRiskComboModifyListener getGenericComboModifyListener() {
	// return relativeRiskComboModifyListener;
	// }

	public Combo getDropDown() {
		return dropDown;
	}
}
