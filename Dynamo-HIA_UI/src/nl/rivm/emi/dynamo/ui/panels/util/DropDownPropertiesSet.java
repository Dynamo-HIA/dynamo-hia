package nl.rivm.emi.dynamo.ui.panels.util;

import java.util.LinkedHashSet;

public class DropDownPropertiesSet extends LinkedHashSet<String> {
		int defaultChoiceIndex = 0;

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

		public boolean contains(String value) {
		boolean found = false;
			for (String item : this) {
				if ((item != null) && (item.equals(value))) {
					found = true;
					break;
				}
			}
			return found;
		}

		public String getSelectedString(int selectedIndex) {
			return (String) this.toArray()[selectedIndex];
		}
	}
