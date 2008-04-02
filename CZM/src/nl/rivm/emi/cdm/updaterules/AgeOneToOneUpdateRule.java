package nl.rivm.emi.cdm.updaterules;

/**
 * Update rule for incrementing the age Characteristic. 
 * Has been made specific to that characteristic.
 * @author mondeelr
 * 
 */
public class AgeOneToOneUpdateRule extends AbstractDoubleBoundUpdateRule {

	int characteristicId;

	/**
	 * NB This is not the stepSize as used in StepSizeSpecific.
	 */
	float stepSize;

	/**
	 * 
	 * @param configuredStepSize
	 *            Age increment per step. Unit: Part of a year.
	 */
	public AgeOneToOneUpdateRule(int characteristicId, float configuredStepSize) {
		super(characteristicId, configuredStepSize);
		setCharacteristicId(characteristicId);
		this.stepSize = configuredStepSize;
	}

	public Float update(Object currentValue) {
		float newAge = (Float) currentValue + stepSize;
		return newAge;
	}
}
