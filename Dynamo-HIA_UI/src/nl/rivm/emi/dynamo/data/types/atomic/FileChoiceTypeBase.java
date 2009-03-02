package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * Base Class for types that have a range in which they are valid.
 *
 * @author mondeelr
 *
 * @param <T>
 */
public abstract class FileChoiceTypeBase extends AtomicTypeBase<String>{
	final protected String baseDirectory;

	protected FileChoiceTypeBase(String elementName, String baseDirectory){
		super(elementName, baseDirectory);
		this.baseDirectory = baseDirectory;
	}
	
	public abstract boolean inRange(String testValue);

	public abstract String fromString(String inputString);

	public abstract String toString(String inputValue);
}
