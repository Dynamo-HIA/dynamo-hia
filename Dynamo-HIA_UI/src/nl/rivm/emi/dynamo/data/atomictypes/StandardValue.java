package nl.rivm.emi.dynamo.data.atomictypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.riskfactor.ContinuousStandardValueRiskFactor;

public class StandardValue {

	/**
	 * Lower limit for the value.
	 */
	static private float lowerLimit = 0F;

	/**
	 * Upper limit for the value.
	 */
	
	static private float upperLimit = Float.MAX_VALUE;

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\d*\\.?\\d*$");

	/**
	 * 
	 * @param value
	 * @return
	 */
	static public boolean isValueValid(String value) {
		Matcher numericalMatcher = matchPattern.matcher((String) value);
		boolean match = numericalMatcher.matches();
		if (match) {
			float floatValue = (Float.valueOf((String) value)).floatValue();
			if (!((lowerLimit <= floatValue) && (floatValue <= upperLimit))) {
				// Abuse match.
				match = false;
			}
		}
		return match;
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	static public boolean isValueValid(Float value) {
		boolean result = true;
			float floatValue = value.floatValue();
			if (!((lowerLimit <= floatValue) && (floatValue <= upperLimit))) {
				result = false;
			}
		return result;
	}
}
