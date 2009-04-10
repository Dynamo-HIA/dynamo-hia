package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;

import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class GenericDropDownPanel {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public Composite parent;
	private Combo dropDown;
	private DropDownPropertiesSet selectablePropertiesSet;
	private int selectedIndex;
	private UpdateDataAction redrawGroupAndUpdateDataAction;
	private GenericComboModifyListener genericComboModifyListener;
	private DynamoTabDataManager owner;

	private String dropDownLabel;
	
	public GenericDropDownPanel(Composite parent, String dropDownLabel,
			int columnSpan, DropDownPropertiesSet selectablePropertiesSet,
			UpdateDataAction redrawGroupAndUpdateDataAction,
			DynamoTabDataManager owner) throws ConfigurationException {
		this.parent = parent;
		this.dropDownLabel = dropDownLabel;
		this.selectablePropertiesSet = selectablePropertiesSet;
		this.redrawGroupAndUpdateDataAction = redrawGroupAndUpdateDataAction;
		this.owner = owner;
		this.dropDownLabel = dropDownLabel;
		
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
			new GenericComboModifyListener(this);
		dropDown.addModifyListener(genericComboModifyListener);

		setDefaultValue();

	}
	
	private void setDefaultValue() throws ConfigurationException {
		// Get the default value
		String currentValue = 
			this.owner.getCurrentValue(this.getLabel());
		// Set the default value
		dropDown.select(getCurrentIndex(currentValue));
	}

	private int getCurrentIndex(String currentValue) {
		return selectablePropertiesSet
		.getSelectedIndex(currentValue);
	}

	public void fill(DropDownPropertiesSet set) {
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}	
	
	public void update(String newText) throws ConfigurationException {
		updateRegisteredDropDown(newText);		
	}

	private void updateRegisteredDropDown(String newText) throws ConfigurationException {
		dropDown.removeAll();
		log.debug("newText" + newText);
		log.debug("this.getLabel()" + this.getLabel());
		DropDownPropertiesSet set = 
			this.owner.getDropDownSet(this.getLabel(), newText);		
		log.debug("SET" + set);
		fill(set);
		dropDown.select(0);
	}

	public void refresh() throws ConfigurationException {		
		dropDown.removeModifyListener(this.genericComboModifyListener);
		dropDown.removeAll();		
		DropDownPropertiesSet set = 
			this.owner.getRefreshedDropDownSet(this.getLabel());		
		log.debug("SET" + set);
		fill(set);
		setDefaultValue();
		dropDown.addModifyListener(this.genericComboModifyListener);		
		// TODO: fire an event for the modify listener to update the dependend drop downs
		
	}
	
	public void updateDataObjectModel(String newText) throws ConfigurationException {
		// TODO Auto-generated method stub
		this.owner.updateObjectState(this.getLabel(), newText);
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
