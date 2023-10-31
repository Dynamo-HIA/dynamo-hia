package nl.rivm.emi.dynamo.data.types.atomic.base;

import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;

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
	protected T MIN_VALUE;
	protected T MAX_VALUE;

	protected NumberRangeTypeBase(String elementName, T minValue, T maxValue){
		super(elementName, minValue);
		MIN_VALUE = minValue;
		MAX_VALUE = maxValue;
	}
	
	public abstract boolean inRange(T testValue);

	public abstract T fromString(String inputString);

	public abstract String toString(T inputValue);

	public T getMIN_VALUE() throws ConfigurationException {
		if((this instanceof ContainerType)&&(MIN_VALUE instanceof Number) && !MIN_VALUE.equals(0)){
			throw new ConfigurationException("Lower range bound should be zero for ContainerTypes.");
		}
		return MIN_VALUE;
	}
	public T getIKnowWhatImDoingMIN_VALUE(){
		return MIN_VALUE;
	}

	public T getMAX_VALUE() {
		return MAX_VALUE;
	}

	/**
	 * Method that provides the upper limit of the counter that controls the
	 * number of default values when the implementing type is a containertype.
	 * 
	 * This standard implementation provides the functionality that was used
	 * before a different limit was needed.
	 * 
	 * @return
	 * @throws ConfigurationException 
	 */
	public T getMaxNumberOfDefaultValues() throws ConfigurationException {
		if((this instanceof ContainerType)&&(MAX_VALUE instanceof Number)){
			throw new ConfigurationException("getMaxNumberOfDefaultValues"
					+ " only makes sense for numeric containertypes.");
		}
		return getMAX_VALUE();
	}
}
