package nl.rivm.emi.cdm.characteristic;

/**
 * Class to contain the configured <code>Characteristic</code>s for the
 * model. User indices are one up, internal storage is zero up.
 * This is a Singleton.
 */
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class CharacteristicsConfigurationMapSingleton extends
		TreeMap<Integer, Characteristic> {
/**
 * The one and only instance of this Class.
 */
	private static CharacteristicsConfigurationMapSingleton instance = null;

	/**
	 * The Class is <code>Serializable</code> and needs s <code>serialVersionUID</code>
	 * to avoid warnings.
	 */
	private static final long serialVersionUID = 2302958719022314338L;

	/**
	 * Private default constructor to enforce singleton-ness.
	 *
	 */
	private CharacteristicsConfigurationMapSingleton() {
		super();
	}

	/**
	 * Method to get the reference to the single instance of this <code>Class</code>.
	 * @return
	 */
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
		@SuppressWarnings("rawtypes")
		Set keySet = this.keySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = keySet.iterator();
		while(iterator.hasNext()){
			iterator.next();
			iterator.remove();
		}
	}
	public String humanReadableReport(){
		StringBuffer resultBuffer = new StringBuffer();
		int characteristicCount = 0;
		for(int index = 0; characteristicCount < size(); index++){
			resultBuffer.append("Characteristic at index " + index + ":");
			Characteristic current = get(Integer.valueOf(index));
if(current == null){
	resultBuffer.append("None\n");
} else {
	resultBuffer.append(current.humanreadableReport() + "\n");
	characteristicCount++;
}
		}
		return resultBuffer.toString();
	}
}
