package nl.rivm.emi.cdm.characteristic;

/**
 * Class to contain the configured characteristics for the
 * model. User indices are one up, internal storage is zero up.
 * This is a Singleton.
 */
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class CharacteristicsConfigurationMapSingleton extends
		TreeMap<Integer, Characteristic> {
	private static CharacteristicsConfigurationMapSingleton instance = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2302958719022314338L;

	private CharacteristicsConfigurationMapSingleton() {
		super();
	}

	public static synchronized CharacteristicsConfigurationMapSingleton getInstance() {
		if (instance == null) {
			instance = new CharacteristicsConfigurationMapSingleton();
		}
		return instance;
	}

	/**
	 * Put a Characteristic Object in the configuration.
	 * 
	 * @param characteristic
	 * @return
	 */
	public Characteristic putCharacteristic(Characteristic characteristic) {
		Integer index = characteristic.getIndex() -1;
		return put(index, characteristic);
	}
	/**
	 * Get a Characteristic Object from the configuration.
	 * 
	 * @param userIndex
	 * @return the Characteristic with the passed id (null if not present).
	 */
	public Characteristic getCharacteristic(int userIndex) {
		return get(userIndex);
	}

	/**
	 * Get a Characteristic Object from the configuration.
	 * 
	 * @param userIndex
	 * @return the Characteristic with the passed id (null if not present).
	 */
	public void clear() {
		Set keySet = this.keySet();
		Iterator iterator = keySet.iterator();
		while(iterator.hasNext()){
			remove(iterator.next());
		}
	}
}
