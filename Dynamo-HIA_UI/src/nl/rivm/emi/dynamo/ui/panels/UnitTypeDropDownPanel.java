package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.listeners.UnitTypeComboModifyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class UnitTypeDropDownPanel {

	Log log = LogFactory.getLog(this.getClass().getName());

	private static final String UNIT = "Unit";
	private static final String MEDIAN_SURVIVAL = "Median Survival";
	private static final String RATE = "Rate";

	public Group group;
	private Label label;
	private Combo dropDown;
	private HelpGroup theHelpGroup;
	private DropDownPropertiesMap selectableExcessMortalityPropertiesMap;
	private UnitTypeComboModifyListener unitTypeModifyListener;
	private int selectedIndex;

	public UnitTypeDropDownPanel(Composite parent, WritableValue writableValue) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		label = new Label(group, SWT.LEFT);
		label.setText(UNIT + ":");
		layoutLabel();
		dropDown = new Combo(group, SWT.DROP_DOWN|SWT.READ_ONLY);
		createDropDownPropertiesMap();
		selectableExcessMortalityPropertiesMap.fillDropDown(dropDown);
		this.unitTypeModifyListener = new UnitTypeComboModifyListener(
				writableValue);
		dropDown.addModifyListener(unitTypeModifyListener);
		String currentValue = unitTypeModifyListener.getCurrentValue();
		int currentIndex = selectableExcessMortalityPropertiesMap
				.getSelectedIndex(currentValue);
		dropDown.select(currentIndex);
		layoutDropDown(label);

	}

	private void createDropDownPropertiesMap() {
		selectableExcessMortalityPropertiesMap = new DropDownPropertiesMap();
		selectableExcessMortalityPropertiesMap.put(MEDIAN_SURVIVAL,
				MEDIAN_SURVIVAL);
		selectableExcessMortalityPropertiesMap.put(RATE, RATE);
	}

	public String getUnitType() {
		return (String) selectableExcessMortalityPropertiesMap
				.getSelectedString(this.selectedIndex);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;
	}

	/**
	 * 
	 * Place the first group in the container
	 * 
	 * @param height
	 */

	public UnitTypeComboModifyListener getUnitTypeModifyListener() {
		return unitTypeModifyListener;
	}

	public Combo getDropDown() {
		return dropDown;
	}

	static class DropDownPropertiesMap extends LinkedHashMap<String, Object> {
		int defaultChoiceIndex = 0;

		public void fillDropDown(Combo dropDown) {
			Set<String> keys = keySet();
			int index = 0;
			for (String item : keys) {
				dropDown.add(item, index);
				index++;
			}
		}

		public int getSelectedIndex(String value) {
			Set<String> keys = keySet();
			int index = 0;
			for (String item : keys) {
				if ((item != null) && (item.equals(value))) {
					break;
				}
				index++;
			}
			if (index >= keys.size()) {
				index = defaultChoiceIndex;
			}
			return index;
		}

		public String getSelectedString(int selectedIndex) {
			return (String) get(selectedIndex);
		}
	}

	/**
	 * Layout stuff.
	 */
	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public void putFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);
	}

	public void putMiddleInContainer(Group topNeighbour, int height) {
		// TODO Auto-generated method stub
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(6, 5 + height);
		group.setLayoutData(formData);
	}

	public void putNextInContainer(Group topNeighbour, int height) {
		// TODO Auto-generated method stub
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		// formData.bottom = new FormAttachment(topNeighbour, 5 + height);
		group.setLayoutData(formData);
	}

	private void layoutLabel() {
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 2);
		// labelFormData.right = new FormAttachment(100, -5);
		labelFormData.top = new FormAttachment(0, 2);
		labelFormData.bottom = new FormAttachment(100, -2);
		label.setLayoutData(labelFormData);
	}

	private void layoutDropDown(Label label) {
		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(label, 5);
		comboFormData.right = new FormAttachment(100, -5);
		comboFormData.top = new FormAttachment(0, 2);
		comboFormData.bottom = new FormAttachment(100, -2);
		dropDown.setLayoutData(comboFormData);
	}
}