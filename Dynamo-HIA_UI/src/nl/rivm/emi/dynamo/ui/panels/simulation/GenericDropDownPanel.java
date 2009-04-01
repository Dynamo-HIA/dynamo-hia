package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class GenericDropDownPanel  {

	Log log = LogFactory.getLog(this.getClass().getName());
	
	public Group group;
	private Combo dropDown;
	private Map selectablePropertiesMap;
	private int selectedIndex;
	private UpdateDataAction redrawGroupAndUpdateDataAction;

	private GenericComboModifyListener genericComboModifyListener;
	
	public GenericDropDownPanel(Group group, String dropDownLabel,
			Map selectablePropertiesMap, 
			UpdateDataAction redrawGroupAndUpdateDataAction) {
		
		this.group = group;
		this.selectablePropertiesMap = selectablePropertiesMap;
		this.redrawGroupAndUpdateDataAction = redrawGroupAndUpdateDataAction;
		
		Label label = new Label(group, SWT.LEFT);
		label.setText(dropDownLabel + ":");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(layoutData);		
		dropDown = new Combo(group, SWT.DROP_DOWN);
		GridData dropLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		dropLayoutData.horizontalSpan = 2;
		dropDown.setLayoutData(dropLayoutData);
		Set<String> keys = this.selectablePropertiesMap.keySet();
		int index = 0;
		for (String item : keys) {
			dropDown.add(item, index);
			index ++;
		}
		/*// For later use with other tab
		dropDown.addSelectionListener(new SelectionAdapter() {
			// In case the user does not select anything
			public void widgetDefaultSelected(SelectionEvent e) {
				//GenericDropDownPanel.this.selectedIndex = dropDown.getSelectionIndex();
			}
			// In case the user makes the selection
			public void widgetSelected(SelectionEvent e) {				
				Combo combo = (Combo) e.getSource();
				int dropDownSelection = combo.getSelectionIndex();
				log.debug("dropDownSelection" + dropDownSelection);
				// (data, Composite)
				GenericDropDownPanel.this.redrawGroupAndUpdateDataAction.updateData(dropDownSelection);
			}
		});*/
		this.genericComboModifyListener = new GenericComboModifyListener();
		dropDown.addModifyListener(genericComboModifyListener);
		dropDown.select(0);		
	}

	// TODO: AtomicTypeObjectTuple OR prepared as String
	private void setDefaultSelection(AtomicTypeObjectTuple tuple) {
		// Set the selected item from the stored values in the xml		
		WritableValue writableValue = (WritableValue) tuple.getValue();
		String stringValue = (String) writableValue.doGetValue();
		
		String[] items = dropDown.getItems();
		String[] newItems = new String[items.length + 1];
		int count = 0;
		for(String item:items){
			newItems[count] = items[count];
			if(item.equals(stringValue)){
				break;
			}
			count++;
		}
		// Nothing found.
		if(count == items.length){
			newItems[count] = stringValue;
			dropDown.setItems(newItems);
			dropDown.select(newItems.length-1);
		} else {
			dropDown.select(count);
		}
	}

	public void handleNextInContainer(Group topNeighbour, int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 15 + height);
		group.setLayoutData(formData);		
	}
	
	public GenericComboModifyListener getGenericComboModifyListener() {
		return genericComboModifyListener;
	}
	
	public Combo getDropDown() {
		return dropDown;
	}
}
