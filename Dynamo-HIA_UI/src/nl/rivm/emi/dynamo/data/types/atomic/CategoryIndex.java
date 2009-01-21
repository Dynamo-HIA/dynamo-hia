package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.markers.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
public class CategoryIndex extends
		FlexibleUpperLimitNumberRangeTypeBase<Integer> implements ContainerType {
	static final protected String XMLElementName = "from";

	static final protected Integer hardUpperLimit = new Integer(9);

	public CategoryIndex() {
		super(XMLElementName, new Integer(1), hardUpperLimit);
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

	static public String getElementName() {
		return XMLElementName;
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
	Object convert4Model(String viewString) {
		Integer modelValue = Integer.decode(viewString);
		return modelValue;
	}

	@Override
	public String convert4View(Object modelValue) {
		String viewValue = ((Integer)modelValue).toString();
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
