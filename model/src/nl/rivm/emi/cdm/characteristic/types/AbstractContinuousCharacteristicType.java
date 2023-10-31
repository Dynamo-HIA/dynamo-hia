package nl.rivm.emi.cdm.characteristic.types;

import java.util.regex.Pattern;

abstract public class AbstractContinuousCharacteristicType extends
		AbstractCharacteristicType {

	Pattern matchPattern;

	Float lowerLimit;

	Float upperLimit;

	protected AbstractContinuousCharacteristicType(String type) {
		super(type);
	}

	public void setLowerLimit(String lowerLimit) {
		this.lowerLimit = Float.valueOf(lowerLimit);
	}

	public void setUpperLimit(String upperLimit) {
		this.upperLimit = Float.valueOf(upperLimit);
	}

	public void setLowerLimit(Float lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(Float upperLimit) {
		this.upperLimit = upperLimit;
	}

	abstract public boolean setLimits(Float lowerLimit, Float upperLimit);

	public Float getLowerLimit() {
		return lowerLimit;
	}

	public Float getUpperLimit() {
		return upperLimit;
	}

	public boolean isCategoricalType() {
		return false;
	}
	/* toegevoegd door hendriek */
	public boolean isCompoundType() {
		return false;
	}


	@Override
	abstract public String humanReadableReport();
}
