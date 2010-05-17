package nl.rivm.emi.cdm.characteristic.types;

/**
 * Type Class for continuous (floating point) numerical types.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueStringParser;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;

public class NumericalContinuousCharacteristicType extends
		AbstractContinuousCharacteristicType {

	static final String myTypeLabel = "numericalcontinuous";

	private float lowerLimit = Float.MIN_VALUE;

	private float upperLimit = Float.MAX_VALUE;

	public NumericalContinuousCharacteristicType() {
		super(myTypeLabel);
		matchPattern = Pattern.compile("^\\-?\\d++\\.?\\d*$");
	}

	@Override
	public boolean isValueValid(Object value) {
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

	@Override
	public boolean setLimits(Float lowerLimit, Float upperLimit) {
		if (lowerLimit != null) {
			this.lowerLimit = ((Float) lowerLimit).floatValue();
		} else {
			lowerLimit = Float.MIN_VALUE;
		}
		if (upperLimit != null) {
			this.upperLimit = ((Float) upperLimit).floatValue();
		} else {
			upperLimit = Float.MAX_VALUE;
		}
		return true;
	}

	@Override
	public String humanReadableReport() {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("Typelabel: " + myTypeLabel + "\n");
		resultBuffer.append("Lower limit: " + lowerLimit + "\n");
		resultBuffer.append("Upper limit: " + upperLimit + "\n");
		return resultBuffer.toString();
	}

	@Override
	public Object convertFromString(String valueAsString,
			int indexInConfiguration) {
		Float value = CharacteristicValueStringParser
				.parseStringToFloat(valueAsString);
		FloatCharacteristicValue charValue = new FloatCharacteristicValue(1, indexInConfiguration,value);
		return charValue;
	}
}
