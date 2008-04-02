package nl.rivm.emi.cdm.characteristic.types;

/**
 * Type Class for continuous (floating point) numerical types.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericalContinuousCharacteristicType extends
		AbstractContinuousCharacteristicType {

	static final String myTypeLabel = "numericalcontinuous";

	private float lowerLimit = Float.MIN_VALUE;

	private float upperLimit = Float.MAX_VALUE;

	public NumericalContinuousCharacteristicType() {
		super(myTypeLabel);
		matchPattern = Pattern.compile("^\\d++\\.?\\d*$");
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
	public boolean setLimits(Object lowerLimit, Object upperLimit) {
		this.lowerLimit = ((Float) lowerLimit).floatValue();
		this.upperLimit = ((Float) upperLimit).floatValue();
		return true;
	}
}
