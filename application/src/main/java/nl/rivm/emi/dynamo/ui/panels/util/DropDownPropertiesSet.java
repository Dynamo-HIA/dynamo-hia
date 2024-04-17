package nl.rivm.emi.dynamo.ui.panels.util;

import java.util.Iterator;
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

	/**
	 * 20090915 Returns empty String instead of null.
	 * 
	 * @param selectedIndex
	 * @return
	 */
	public String getSelectedString(int selectedIndex) {
		String selectedString = "";
		if (this.size() > 0) {
			selectedString = (String) this.toArray()[selectedIndex];
		}
		return selectedString;
	}

	public String[] toArray() {
		String[] resultArray = new String[this.size()];
		Iterator<String> iterator = this.iterator();
		for (int count = 0; count < this.size(); count++) {
			resultArray[count] = iterator.next();
		}
		return resultArray;
	}
}
