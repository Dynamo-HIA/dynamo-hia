package nl.rivm.emi.cdm.individual;

import java.util.ArrayList;
import java.util.Iterator;

import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.characteristic.IntCharacteristicValue;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * Simplest individual that can be used in a simulation.
 * 
 * @author mondeelr
 * 
 */
public class Individual extends ArrayList<IntCharacteristicValue> {
	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Name of the tag that represents an Individual in configuration XML-files.
	 * 
	 */
	private String elementName;

	private String label = "Not initialized.";

	protected Individual(String elementName, String label) {
		super();
		this.elementName = elementName;
		this.label = label;
	}

	public String getElementName() {
		return elementName;
	}

	public int getCurrentCharacteristicValue(int characteristicIndex)
			throws CZMRunException {
		IntCharacteristicValue cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CZMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		}
		return cv.getCurrentValue();
	}

	public void updateCharacteristicValue(int characteristicIndex,
			int newValue) throws CZMRunException {
		IntCharacteristicValue cv = this.get(characteristicIndex);
		if (cv == null) {
			throw new CZMRunException(
					"No CharacteristicValue found for index: "
							+ characteristicIndex);
		}
		cv.appendValue(newValue);
	}

	/**
	 * Append / replace the value at index.
	 */
	public IntCharacteristicValue luxeSet(int index,
			IntCharacteristicValue value) {
		IntCharacteristicValue result = null;
		if (index >= this.size()) {
			int count = this.size();
			// Fill up.
			for (; count < index; count++) {
				add(count, null);
			}
			add(count, value);
		} else {
			log.warn("Replacing CharacteristicValue.");
			result = this.get(index);
			set(index, value);
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

	public Iterator<IntCharacteristicValue> iterator() {
		return new CharacteristicValueIterator();
	}

	class CharacteristicValueIterator implements
			Iterator<IntCharacteristicValue> {
		/**
		 * Some status bookkeeping.
		 */
		int currentIndex = -1;

		int nextIndex = -1;
		
		int lastReturnedIndex = -1;

		public boolean hasNext() {
			nextIndex = -1; // Invalidate.
			for (int count = currentIndex + 1; count < size(); count++) {
				if (get(count) != null) {
					nextIndex = count;
					break;
				}
			}
			return (nextIndex != -1);
		}

		public IntCharacteristicValue next() {
			IntCharacteristicValue found = null;
			// Sanity check.
			if (-1 < nextIndex && nextIndex < size()) {
				currentIndex = nextIndex;
				found = get(currentIndex);
				if(found != null){
					lastReturnedIndex = currentIndex;
				}
			}
			return found;
		}

		public void remove(){
			set(lastReturnedIndex, null);
		}
	}
}
