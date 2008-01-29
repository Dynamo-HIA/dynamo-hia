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
import java.util.HashMap;

public class CharacteristicsConfigurationMap extends HashMap<Integer, Characteristic>{
}
