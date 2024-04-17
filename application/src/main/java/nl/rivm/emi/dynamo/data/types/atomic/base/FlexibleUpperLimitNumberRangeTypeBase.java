package nl.rivm.emi.dynamo.data.types.atomic.base;




public abstract class FlexibleUpperLimitNumberRangeTypeBase<T> extends
		NumberRangeTypeBase<T>  {

	protected FlexibleUpperLimitNumberRangeTypeBase(String elementName,
			T minValue, T maxValue){
		super(elementName, minValue, maxValue);
	}

	public T setMAX_VALUE(T newUpperLimit){
		T oldUpperLimit = MAX_VALUE;
		MAX_VALUE = newUpperLimit;
		return oldUpperLimit;
	}
}
