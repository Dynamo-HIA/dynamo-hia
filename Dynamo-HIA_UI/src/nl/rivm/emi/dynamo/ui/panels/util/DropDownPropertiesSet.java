package nl.rivm.emi.dynamo.ui.panels.util;

import java.util.LinkedHashSet;

import org.eclipse.swt.widgets.Combo;

public class DropDownPropertiesSet extends LinkedHashSet<String> {
		int defaultChoiceIndex = 0;

		public void fillDropDown(Combo dropDown) {
			int index = 0;
			for (String item : this) {
				dropDown.add(item, index);
				index++;
			}
		}

		public int getSelectedIndex(String value) {
			int index = 0;
			for (String item : this) {
				if ((item != null) && (item.equals(value))) {
					break;
				}
				index++;
			}
			if (index >= size()) {
				index = defaultChoiceIndex;
			}
			return index;
		}

		public String getSelectedString(int selectedIndex) {
			return (String) this.toArray()[selectedIndex];
		}
	}
