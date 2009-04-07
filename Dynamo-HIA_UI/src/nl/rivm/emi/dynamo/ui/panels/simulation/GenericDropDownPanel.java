package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
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
	
	public GenericDropDownPanel(Composite parent, String dropDownLabel,
			int columnSpan, DropDownPropertiesSet selectablePropertiesSet,
			Object object,
			UpdateDataAction redrawGroupAndUpdateDataAction) {		
		this.parent = parent;
		this.selectablePropertiesSet = selectablePropertiesSet;
		this.redrawGroupAndUpdateDataAction = redrawGroupAndUpdateDataAction;
		
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
		
		// Create the modify listener object
		if (object instanceof WritableValue) {		
			this.genericComboModifyListener = 
				new GenericComboModifyListener((WritableValue) object);	
		} else {
			
		}
		dropDown.addModifyListener(genericComboModifyListener);

		// Get the default value
		String currentValue = genericComboModifyListener.getCurrentValue();
		int currentIndex = selectablePropertiesSet
				.getSelectedIndex(currentValue);
		// Set the default value
		dropDown.select(currentIndex);

	}
	
	public void fill(DropDownPropertiesSet set) {
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}	
	
	public GenericComboModifyListener getGenericComboModifyListener() {
		return genericComboModifyListener;
	}
	
	public Combo getDropDown() {
		return dropDown;
	}
	
}
