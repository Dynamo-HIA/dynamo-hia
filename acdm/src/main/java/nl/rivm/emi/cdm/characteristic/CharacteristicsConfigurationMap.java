package nl.rivm.emi.cdm.characteristic;

/**
 * Class to contain the configured <code>Characteristic</code>s for the
 * model. User indices are one up, internal storage is zero up.
 * This is a Singleton.
 */
import java.util.TreeMap;

public class CharacteristicsConfigurationMap extends
		TreeMap<Integer, Characteristic> {

	/**
	 * The Class is <code>Serializable</code> and needs s <code>serialVersionUID</code>
	 * to avoid warnings.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public CharacteristicsConfigurationMap() {
		super();
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

// Not needed in this non-singleton version.
	/**
	 * Get a Characteristic Object from the configuration.
	 * 
	 * @param userIndex
	 * @return the Characteristic with the passed id (null if not present).
	 */
//	public void clear() {
//		Set keySet = this.keySet();
//		Iterator iterator = keySet.iterator();
//		while(iterator.hasNext()){
//			iterator.next();
//			iterator.remove();
//		}
//	}

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
