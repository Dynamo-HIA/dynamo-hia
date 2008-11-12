package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Value extends NumberRangeTypeBase<Float> {

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\d*\\.?\\d*$");

	public Value(){
		super("value", 0F, Float.MAX_VALUE);
	}
	
	public boolean inRange(Float testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}
	public Float fromString(String inputString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString(Float inputValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
