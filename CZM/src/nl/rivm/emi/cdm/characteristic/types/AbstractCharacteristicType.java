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
	final private String typeString;

	public AbstractCharacteristicType(String typeString) {
		super();
		this.typeString = typeString;
	}

	public String getTypeString() {
		return typeString;
	}

	abstract public boolean isValueValid(Object value);

	abstract public boolean isCategoricalType();
	
	
	/* toegevoegd by hendriek */
	abstract public boolean isCompoundType();

	abstract public String humanReadableReport();
}
