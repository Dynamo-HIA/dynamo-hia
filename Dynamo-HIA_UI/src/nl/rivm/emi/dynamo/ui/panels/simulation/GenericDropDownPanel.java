package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class GenericDropDownPanel  {

	public Group group;
	private Combo dropDown;
	private Map selectablePropertiesMap;
	private int selectedIndex;
	
	private GenericComboModifyListener genericComboModifyListener;
	
	public GenericDropDownPanel(Group group, String dropDownLabel,
			Map selectablePropertiesMap, Object object) {
		this.group = group;
		this.selectablePropertiesMap = selectablePropertiesMap;
		Label label = new Label(group, SWT.LEFT);
		label.setText(dropDownLabel + ":");
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 2);
		// labelFormData.right = new FormAttachment(100, -5);
		labelFormData.top = new FormAttachment(0, 2);
		labelFormData.bottom = new FormAttachment(100, -2);
		label.setLayoutData(labelFormData);		
		dropDown = new Combo(group, SWT.DROP_DOWN);
		Set<String> keys = this.selectablePropertiesMap.keySet();
		int index = 0;
		for (String item : keys) {
			dropDown.add(item, index);
			index ++;
		}
		dropDown.addSelectionListener(new SelectionAdapter() {
			// In case the user does not select anything
			public void widgetDefaultSelected(SelectionEvent e) {
				GenericDropDownPanel.this.selectedIndex = dropDown.getSelectionIndex();
			}
			// In case the user makes the selection
			public void widgetSelected(SelectionEvent e) {
				GenericDropDownPanel.this.selectedIndex = dropDown.getSelectionIndex();
			}
		});
		this.genericComboModifyListener = new GenericComboModifyListener();
		dropDown.addModifyListener(genericComboModifyListener);
		dropDown.select(0);
		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(label, 5);
		comboFormData.right = new FormAttachment(100, -5);
		comboFormData.top = new FormAttachment(0, 2);
		comboFormData.bottom = new FormAttachment(100, -2);
		dropDown.setLayoutData(comboFormData);		

	}

	public void handleFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);		
	}
	
	private void setDefaultSelection() {
		// Set the selected item from the stored values in the xml
		AtomicTypeObjectTuple tuple = null;		////TODO provide tuple or HashMap  = (AtomicTypeObjectTuple) genericDataObject.get(XMLTagEntityEnum.UNITTYPE.getElementName());
		WritableValue writableValue = (WritableValue) tuple.getValue();
		String stringValue = (String) writableValue.doGetValue();
		String[] items = dropDown.getItems();
		String[] newItems = new String[items.length+1];
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
		group.setLayoutData(formData);		
	}
	

}
