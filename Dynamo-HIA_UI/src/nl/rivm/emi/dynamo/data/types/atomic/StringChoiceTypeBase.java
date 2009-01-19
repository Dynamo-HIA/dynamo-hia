package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * Base Class for types that have a range in which they are valid.
 * 
 * 20081127 Removed final from MAX_VALUE for flexible use.
 * 
 * @author mondeelr
 *
 * @param <T>
 */
public abstract class StringChoiceTypeBase extends AtomicTypeBase<String>{
	final protected String[] possibleChoices;

	protected StringChoiceTypeBase(String elementName, String[] possibleChoices){
// Initialize with a default value.
		super(elementName, possibleChoices[0]);
		this.possibleChoices = possibleChoices;
	}
	
	public abstract boolean inRange(String testValue);

	public abstract String fromString(String inputString);

	public abstract String toString(String inputValue);
}
