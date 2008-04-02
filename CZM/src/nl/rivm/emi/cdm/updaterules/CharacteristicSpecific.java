package nl.rivm.emi.cdm.updaterules;

/**
 * Interface to be implemented by UpdateRules that have been created to update a
 * specific Characteristic.
 * 
 * The id embedded in the UpdateRule will be used for selecting UpdateRules for
 * a certain Characteristic later.
 * 
 * Software using the UpdateRule is responsible for assigning the result to the
 * correct Characteristic.
 * 
 * @author mondeelr
 * 
 */
public interface CharacteristicSpecific {

 	public int getCharacteristicId();

	public void setCharacteristicId(int characteristicId);
}