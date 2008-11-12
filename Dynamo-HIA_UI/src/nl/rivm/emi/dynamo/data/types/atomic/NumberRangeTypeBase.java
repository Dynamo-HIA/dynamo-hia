package nl.rivm.emi.dynamo.data.types.atomic;

public abstract class NumberRangeTypeBase<T> extends AtomicTypeBase<T>{
	final protected T MIN_VALUE;
	final protected T MAX_VALUE;

	protected NumberRangeTypeBase(String tagName, T minValue, T maxValue){
		super(tagName, minValue);
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
