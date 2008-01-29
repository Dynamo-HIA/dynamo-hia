package nl.rivm.emi.cdm.individual;

import java.util.ArrayList;

import nl.rivm.emi.cdm.characteristic.CharacteristicValue;
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
public class Individual extends ArrayList<CharacteristicValue> {
	Log log = LogFactory.getLog(getClass().getName());

	/**
	 * Name of the tag that represents an Individual in configuration XML-files.
	 * 
	 */
	private String label = "Not initialized.";

	// private Integer currentCharacteristicValue;

	public Individual(String label) {
		super();
		this.label = label;
	}

	public CharacteristicValue getCharacteristicValue(int characteristicIndex) {
		return this.get(characteristicIndex);
	}

	public boolean updateCharacteristicValue(int characteristicIndex,
			CharacteristicValue value) {
		boolean success = false;
		if (get(characteristicIndex) != null) {
			this.luxeSet(characteristicIndex, value);
			success = true;
		}
		return success;
	}

	/**
	 * Append / replace the value at index.
	 */
	public CharacteristicValue luxeSet(int index, CharacteristicValue value) {
		CharacteristicValue result = null;
		if (index >= this.size()) {
			int count = this.size();
			// Fill up.
			for (; count < index; count++) {
				set(count, null);
			}
			add(count, value);
		} else {
			result = this.get(index);
			set(index, value);
		}
		return result;
	}
}
