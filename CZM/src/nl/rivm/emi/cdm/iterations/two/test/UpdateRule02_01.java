package nl.rivm.emi.cdm.iterations.two.test;

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
public class UpdateRule02_01 extends OneToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific {

	private int characteristicId;

	private float stepSize;

	/**
	 * Default constructor for loading via classname.
	 */
	public UpdateRule02_01() {
		super();
	}

	public UpdateRule02_01(int characteristicId) {
		super();
		setCharacteristicId(characteristicId);
	}

	public Integer update(Object currentValue) throws CDMUpdateRuleException {
		if (currentValue instanceof Integer) {
			return (Integer) currentValue;
		} else {
			throw new CDMUpdateRuleException(
					String.format(CDMUpdateRuleException.wrongParameterMessage, currentValue
									.getClass().getSimpleName()));
		}
	}

	public int getCharacteristicId() {
		return this.characteristicId;
	}

	public void setCharacteristicId(int characteristicId) {
		this.characteristicId = characteristicId;
	}

	public float getStepSize() {
		return stepSize;
	}

	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}
}
