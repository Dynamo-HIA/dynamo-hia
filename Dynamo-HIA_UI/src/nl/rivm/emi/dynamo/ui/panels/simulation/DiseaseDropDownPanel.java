package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class DiseaseDropDownPanel extends GenericDropDownPanel {

	/**
	 * Beware, DiseaseDropDownPanels start their life without a ModifyListener,
	 * unlike the others.
	 * 
	 * @param parent
	 * @param dropDownLabel
	 * @param columnSpan
	 * @param selectablePropertiesSet
	 * @param dataManager
	 * @param helpGroup
	 * @throws ConfigurationException
	 */
	public DiseaseDropDownPanel(Composite parent, String dropDownLabel,
			int columnSpan, DropDownPropertiesSet selectablePropertiesSet,
			DynamoTabDataManager dataManager, HelpGroup helpGroup)
			throws ConfigurationException {
		super(parent, dropDownLabel, columnSpan, selectablePropertiesSet,
				dataManager, helpGroup);
		goDeaf();
	}

	@Override
	protected int getCurrentIndex(String currentValue) {
		int currentIndex = 0; // When current value exists, select the first
		// entry
		if (currentValue != null) {
			currentIndex = selectablePropertiesSet
					.getSelectedIndex(currentValue);
		}
		return currentIndex;
	}

	public void update(String newText) throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		updateMyDropDown(newText);
	}

	/**
	 * Update the registered property sets after selection
	 * 
	 * @param newText
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 * @throws DynamoNoValidDataException
	 */
	@Override
	protected void updateMyDropDown(String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		goDeaf();
		dropDown.removeAll();
		log.debug("newText" + newText);
		log.debug("this.getLabel()" + this.getLabel());
		this.selectablePropertiesSet.clear();
		this.selectablePropertiesSet.addAll(this.myDataManager.getDropDownSet(
				this.getLabel(), newText));
		log.debug("Set " + this.selectablePropertiesSet);
		fill(this.selectablePropertiesSet);
		dropDown.select(0);
		updateDataObjectModel(dropDown.getItem(0));
		goListen();
	}

	public void fill(DropDownPropertiesSet set) {
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}

	/**
	 * 
	 * Refresh list after tab change, only for the selection group
	 * 
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 * @throws DynamoNoValidDataException
	 */
	@Override
	public void refresh() throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		log.debug("REFRESH");
		goDeaf();
		this.selectablePropertiesSet.clear();
		this.selectablePropertiesSet.addAll(this.myDataManager.getDropDownSet(
				this.getLabel(), null));
		log.debug("Set: " + this.selectablePropertiesSet);
		// fill(this.selectablePropertiesSet);
		refresh(this.selectablePropertiesSet);
		// Remove old value (is choosable again)
		this.myDataManager.removeOldDefaultValue(this.getLabel());
		if (!((DiseaseTabDataManager) myDataManager).getMyTabPlatform()
				.getListenerWorking()) {
			goListen();
		}
		// Set the new default (can be the same value as the removed one)
		setDefaultValue();
		
		if (((DiseaseTabDataManager) myDataManager).getMyTabPlatform()
				.getListenerWorking()) {
			goListen();
		}
	}

	@Override
	protected void setDefaultValue() throws ConfigurationException {
		// Get the default value
		String currentValue = myDataManager
				.getValueFromSingleConfiguration(this.getLabel());
		log.debug("setDefaultValue() for label: " + this.getLabel()
				+ " currentValue: " + currentValue);
		log.debug("getCurrentIndex(currentValue)"
				+ getCurrentIndex(currentValue));

		// Retrieve the index value
		int index = getCurrentIndex(currentValue);

		// Set the default value
		dropDown.select(index);
		if (currentValue != null)
			this.myDataManager.setDefaultValue(this.getLabel(), currentValue);
	}

	/**
	 * Zap the dropdown-list and fill it.
	 * 
	 * @param set
	 */
	public void refresh(DropDownPropertiesSet set) {
		dropDown.removeAll();
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}

	public void updateDataObjectModel(String newText)
			throws ConfigurationException, NoMoreDataException {
		// Remove old value (is choosable again)
		String oldValue = myDataManager
				.getValueFromSingleConfiguration(getLabel());
		if ((oldValue != null) && (!oldValue.equals(newText))) {
			this.myDataManager.removeOldDefaultValue(this.getLabel());
			// Add new value
			this.myDataManager.updateObjectState(this.getLabel(), newText);
		}
	}

	public String getLabel() {
		return this.dropDownLabel;
	}

	public GenericComboModifyListener getGenericComboModifyListener() {
		return genericComboModifyListener;
	}

	public Combo getDropDown() {
		return dropDown;
	}

	public String getParentValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void goListen() {
		dropDown.addModifyListener(genericComboModifyListener);
	}

	public void goDeaf() {
		dropDown.removeModifyListener(genericComboModifyListener);
	}
}
