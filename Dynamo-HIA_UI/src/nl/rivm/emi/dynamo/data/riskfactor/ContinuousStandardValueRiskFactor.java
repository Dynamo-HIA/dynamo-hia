package nl.rivm.emi.dynamo.data.riskfactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.characteristic.types.AbstractContinuousCharacteristicType;

public class ContinuousStandardValueRiskFactor extends
		AbstractContinuousCharacteristicType implements RiskFactorMarker {

	static final String myTagLabel = "continuous";

	private float lowerLimit = 0F;

	private float upperLimit = Float.MAX_VALUE;

	public ContinuousStandardValueRiskFactor() {
		super(ContinuousStandardValueRiskFactor.myTagLabel,
				"^\\-?\\d++\\.?\\d*$");
	}

	public boolean isValueValid(Object value) {
	Pattern matchPattern = Pattern.compile(testPattern);
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
 * Dummy method to satisfy override contract.
 */
	@Override
	public boolean setLimits(Float lowerLimit, Float upperLimit) {
//		if (lowerLimit != null) {
//			this.lowerLimit = ((Float) lowerLimit).floatValue();
//		} else {
//			lowerLimit = Float.MIN_VALUE;
//		}
//		if (upperLimit != null) {
//			this.upperLimit = ((Float) upperLimit).floatValue();
//		} else {
//			upperLimit = Float.MAX_VALUE;
//		}
		return true;
	}

	@Override
	public String humanReadableReport() {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("Typelabel: " + myTagLabel + "\n");
		resultBuffer.append("Lower limit: " + lowerLimit + "\n");
		resultBuffer.append("Upper limit: " + upperLimit + "\n");
		return resultBuffer.toString();
	}

}
