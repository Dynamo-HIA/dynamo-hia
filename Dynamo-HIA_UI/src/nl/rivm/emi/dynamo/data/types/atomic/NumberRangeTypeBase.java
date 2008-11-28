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
public abstract class NumberRangeTypeBase<T> extends AtomicTypeBase<T>{
	final protected T MIN_VALUE;
	protected T MAX_VALUE;

	protected NumberRangeTypeBase(String elementName, T minValue, T maxValue){
		super(elementName, minValue);
		MIN_VALUE = minValue;
		MAX_VALUE = maxValue;
	}
	
	public abstract boolean inRange(T testValue);

	public abstract T fromString(String inputString);

	public abstract String toString(T inputValue);

	public T getMIN_VALUE() {
		return MIN_VALUE;
	}

	public T getMAX_VALUE() {
		return MAX_VALUE;
	}


}
