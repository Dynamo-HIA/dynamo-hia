package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
abstract public class AbstractClassIndex extends FlexibleUpperLimitNumberRangeTypeBase<Integer>{


	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern.compile("^\\d*$");

	static final protected Integer hardLowerLimit = new Integer(1);
	static final protected Integer hardUpperLimit = new Integer(10);


	/**
	 * Constructor used for overriding.
	 * 
	 * @param myElementName
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public AbstractClassIndex(String myElementName, Integer lowerLimit, Integer upperLimit){
		super(myElementName, Math.max(hardLowerLimit,lowerLimit), Math.min(hardUpperLimit,upperLimit));
	}

	public Integer getDefaultValue() {
		return new Integer(1);
	}
	
	public boolean inRange(Integer testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}

	public Integer fromString(String inputString) {
		Integer result = null;
		try {
			result = Integer.decode(inputString);
			if (!inRange(result)) {
				result = null;
			}
			return result;
		} catch (NumberFormatException e) {
			result = null;
			return result;
		}
	}

	public String toString(Integer inputValue) {
		return Integer.toString(inputValue.intValue());
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public Integer setMAX_VALUE(Integer newUpperLimit) {
		Integer oldUpperLimit = null;
		if ((hardUpperLimit.compareTo(newUpperLimit) >= 0)
				&& (MIN_VALUE.compareTo(newUpperLimit) < 0)) {
			oldUpperLimit = MAX_VALUE;
			MAX_VALUE = newUpperLimit;
		}
		return oldUpperLimit;
	}

	@Override
	public Object convert4Model(String viewString) {
		Integer modelValue = Integer.decode(viewString);
		return modelValue;
	}

	@Override
	public String convert4View(Object modelValue) {
		String viewValue = ((Integer) modelValue).toString();
		return viewValue;
	}

	@Override
	public String convert4File(Object modelValue) {
		Integer nakedValue = null;
		if(modelValue instanceof WritableValue){
		nakedValue =  (Integer)((WritableValue)modelValue).doGetValue();
		} else {
			nakedValue = (Integer) modelValue;
		}
		String viewValue =  convert4View(nakedValue);
			return viewValue;
	}

	@Override
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		// TODO Auto-generated method stub
		return null;
	}
}
