package nl.rivm.emi.cdm.characteristic.types;

/**
 * Base-Class for the various Characteristic types.
 * 
 * @author mondeelr
 *
 */
abstract public class AbstractCharacteristicType {
	/**
	 * The String that identifies this CharacteristicType in configuration
	 * files.
	 */
	private String typeString;

	public AbstractCharacteristicType(String typeString) {
		super();
		this.typeString = typeString;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	abstract public boolean isValueValid(Object value); 

	abstract public boolean isCategoricalType();
}
