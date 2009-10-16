package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * The generic drop down present in all tabs
 * 
 * @author schutb
 *
 */
public class GenericDropDownPanel {

	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	public Composite parent;
	protected Combo dropDown;
	protected DropDownPropertiesSet selectablePropertiesSet;
//	private int selectedIndex;
//	private UpdateDataAction redrawGroupAndUpdateDataAction;
	protected GenericComboModifyListener genericComboModifyListener;
	protected DynamoTabDataManager myDataManager;
	protected HelpGroup helpGroup;

	protected String dropDownLabel;
	
	public GenericDropDownPanel(Composite parent, String dropDownLabel,
			int columnSpan, DropDownPropertiesSet selectablePropertiesSet,
			DynamoTabDataManager dataManager,
			HelpGroup helpGroup) throws ConfigurationException {
		this.parent = parent;
		this.dropDownLabel = dropDownLabel;
		this.selectablePropertiesSet = selectablePropertiesSet;
//		this.redrawGroupAndUpdateDataAction = redrawGroupAndUpdateDataAction;
		this.myDataManager = dataManager;
		this.dropDownLabel = dropDownLabel;
		this.helpGroup = helpGroup;
		
		Label label = new Label(parent, SWT.LEFT);
		label.setText(dropDownLabel + ":");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(layoutData);		
		dropDown = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
		GridData dropLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		dropLayoutData.horizontalSpan = columnSpan;
		//dropLayoutData.marginHeight = 0;
		dropDown.setLayoutData(dropLayoutData);
		this.fill(selectablePropertiesSet);
		this.genericComboModifyListener = 
			new GenericComboModifyListener(this, this.helpGroup);
		dropDown.addModifyListener(genericComboModifyListener);
		setDefaultValue();
	}
	
	protected void setDefaultValue() throws ConfigurationException {
		// Get the default value
		String currentValue = 
			myDataManager.getValueFromSingleConfiguration(this.getLabel());
		log.debug("CURRENTVALUEDEF: " + currentValue);
		log.debug("getCurrentIndex(currentValue)" + getCurrentIndex(currentValue));
		
		// Retrieve the index value
		int index = getCurrentIndex(currentValue);		
		
		// Set the default value
		dropDown.select(index);
		if (currentValue != null) 
			this.myDataManager.setDefaultValue(this.getLabel(), currentValue);
	}

	protected int getCurrentIndex(String currentValue) {
		// No current value exists, select the first entry
		if (currentValue == null)
			return 0;
		log.debug("selectablePropertiesSet" + selectablePropertiesSet);
		return selectablePropertiesSet
			.getSelectedIndex(currentValue);
	}

	public void update(String newText) throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		updateMyDropDown(newText);		
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
	protected void updateMyDropDown(String newText) throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		dropDown.removeAll();
		log.debug("newText" + newText);
		log.debug("this.getLabel()" + this.getLabel());
		this.selectablePropertiesSet.clear();
		this.selectablePropertiesSet.addAll( 
			this.myDataManager.getDropDownSet(this.getLabel(), newText));		
		log.debug("Set " + this.selectablePropertiesSet);
		fill(this.selectablePropertiesSet);
		dropDown.select(0);
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
	public void refresh() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		log.debug("REFRESH");
		dropDown.removeModifyListener(this.genericComboModifyListener);
		this.selectablePropertiesSet.clear();
		this.selectablePropertiesSet.addAll( 
			this.myDataManager.getRefreshedDropDownSet(this.getLabel()));		
		log.debug("Set: " + this.selectablePropertiesSet);
		// fill(this.selectablePropertiesSet);
		refresh(this.selectablePropertiesSet);
		// Remove old value (is choosable again)
		this.myDataManager.removeOldDefaultValue(this.getLabel());
		// Set the new default (can be the same value as the removed one)
		setDefaultValue();
		dropDown.addModifyListener(this.genericComboModifyListener);		
		// TODO: fire an event for the modify listener to update the dependend drop downs
	}

	/**
	 * Zap the dropdown-list and fill it.
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
		
	public void updateDataObjectModel(String newText) throws ConfigurationException, NoMoreDataException {
		// Remove old value (is choosable again)
		this.myDataManager.removeOldDefaultValue(this.getLabel());
		// Add new value
		this.myDataManager.updateObjectState(this.getLabel(), newText);
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
	
}
