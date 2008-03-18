package nl.rivm.emi.cdm.updating;

/**
 * Class administrating the usage of Characteristics in the update-rule it
 * belongs to. TODO When more Characteristics have been added after the
 * Signature was instantiated the Signature cannot work with them.
 * 
 * @author mondeelr
 * 
 */
public class Signature {
	/**
	 * Indicator whether a Characteristic is used in the updaterule.
	 */
	boolean flags[];

	/**
	 * Allocate the flags for the number of Characteristics present and
	 * initialise them all to false (Characteristic not used).
	 * 
	 * @param numberOfCharacteristics
	 */
	public Signature(int numberOfCharacteristics) {
		flags = new boolean[numberOfCharacteristics];
		for (int count = 0; count < flags.length; count++) {
			flags[count] = false;
		}
	}

	/**
	 * Set the flag to true for the Characteristic that has the passed id.
	 * 
	 * @param id
	 * @return false when the Characteristic could not be registered, true
	 *         otherwise.
	 */
	public boolean registerCharacteristic(int id) {
		boolean result = false;
		if (flags.length >= id) {
			flags[id - 1] = true;
			result = true;
		}
		return result;
	}

	/**
	 * Reset the flag to false for the Characteristic that has the passed id.
	 * 
	 * @param id
	 * @return false when the Characteristic could not be unRegistered, true
	 *         otherwise.
	 */
	public boolean unRegisterCharacteristic(int id) {
		boolean result = false;
		if (flags.length >= id) {
			flags[id - 1] = false;
			result = true;
		}
		return result;
	}

	/**
	 * Indicates whether the updaterules that has this signature uses the
	 * Characteristic with the passed index.
	 * 
	 * @param id
	 * @return
	 */
	public boolean usesCharacteristic(int id) {
		boolean result = false;
		if (flags.length >= id) {
			result = flags[id - 1];
		}
		return result;

	}
}
