package nl.rivm.emi.cdm.characteristic;

/**
 * Class to contain the configured characteristics for the
 * model.
 * It is serialized to XML format:
 * <characteristics>
 *    <characteristic>
 *       <index>
 *       </index>
 *       <label>
 *       </label> 
 *    </characteristic> 
 * </characteristics> 
 */
import java.util.TreeMap;

public class CharacteristicsConfigurationMap extends
		TreeMap<Integer, Characteristic> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2302958719022314338L;

	public CharacteristicsConfigurationMap() {
		super();
	}

	public Characteristic addCharacteristic(Characteristic characteristic) {
		Integer index = characteristic.getIndex();
		Characteristic PriorCharacteristic = get(index);
		put(index, characteristic);
		return PriorCharacteristic;
	}
}
