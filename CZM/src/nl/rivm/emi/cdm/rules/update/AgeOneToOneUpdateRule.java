package nl.rivm.emi.cdm.rules.update;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;

/**
 * Update rule for incrementing the age Characteristic. Has been made specific
 * to that characteristic.
 * 
 * @author mondeelr
 * 
 */
public class AgeOneToOneUpdateRule extends OneToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific {

	private int characteristicId;

	private float stepSize;

	/**
	 * Default constructor for ClassLoading.
	 */
	public AgeOneToOneUpdateRule() {
		super();
	}

	/**
	 * 
	 * @param configuredStepSize
	 *            Age increment per step. Unit: Part of a year.
	 */
	public AgeOneToOneUpdateRule(int characteristicId, float configuredStepSize) {
		super();
		this.characteristicId = characteristicId;
		this.stepSize = configuredStepSize;
		setCharacteristicId(characteristicId);
		this.stepSize = configuredStepSize;
	}

	public int getCharacteristicId() {
		return this.characteristicId;
	}

	public void setCharacteristicId(int characteristicId) {
		this.characteristicId = characteristicId;
	}

	public float getStepSize() {
		return this.stepSize;
	}

	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}

	public Float update(Object currentValue) throws CDMUpdateRuleException {
		if (currentValue instanceof Float) {
			float newAge = (Float) currentValue + stepSize;
			return newAge;
		} else {
			throw new CDMUpdateRuleException(String.format(
					CDMUpdateRuleException.wrongParameterMessage, currentValue.getClass()
							.getSimpleName()));
		}
	}
}
