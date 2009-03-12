package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;

public class Age extends NumberRangeTypeBase<Integer> implements ContainerType {
	static final protected String XMLElementName = "age";
	static final public Integer MINAGE = 0;
	static final public Integer MAXAGE = 95;

	public Age() {
		super(XMLElementName, MINAGE, MAXAGE);
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
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Object convert4Model(String viewString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String convert4View(Object modelValue) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Integer getDefaultValue() {
		return 0;
	}

}
