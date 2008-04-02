package nl.rivm.emi.cdm.characteristic.types;

import java.util.regex.Pattern;


abstract public class AbstractContinuousCharacteristicType extends
		AbstractCharacteristicType {

	Pattern matchPattern;

	protected AbstractContinuousCharacteristicType(String type) {
		super(type);
	}
	abstract public boolean setLimits(Object lowerLimit, Object upperLimit);

	public boolean isCategoricalType(){
		return false; 
	}
}
