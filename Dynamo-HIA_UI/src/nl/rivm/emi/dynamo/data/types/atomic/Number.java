package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

public class Number extends NumberRangeTypeBase<Integer> implements LeafType<Integer>{
	static final protected String XMLElementName = "number";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\d*$");

	public Number(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString(Integer inputValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getDefaultValue() {
		return 0;
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
}
