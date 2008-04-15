package nl.rivm.emi.cdm.updaterules.obsolete;

import nl.rivm.emi.cdm.updaterules.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.updaterules.base.StepSizeSpecific;
import nl.rivm.emi.cdm.updaterules.base.UpdateRuleMarker;

/**
 * BaseClass for updaterules, providing storage for the index of the
 * Characteristic the UpdateRule is meant to update and the stepsize the
 * updaterule pertains to.
 * 
 * @author mondeelr
 * @deprecated
 */
public class AbstractDoubleBoundUpdateRule implements UpdateRuleMarker, CharacteristicSpecific, StepSizeSpecific{
	/**
	 * The characteristic-id's start from 1 (one). An ID of -1 indicates the
	 * rule is useable for any characteristic. 0 Indicates the UpdateRule has
	 * not been initialised. non-useable update-rule.
	 */
	int characteristicId = 0;

	/**
	 * A stepsize of -1 means the rule goes for all stepsizes. 0 Indicates the
	 * UpdateRule has not been initialised. TODO QUESTION What should the step
	 * timebase be? (day, month....)
	 */
	float stepSize = 0;

	private AbstractDoubleBoundUpdateRule() {
		super();
	}

	public AbstractDoubleBoundUpdateRule(int characteristicId, float stepSize) {
		this.characteristicId = characteristicId;
		this.stepSize = stepSize;
	}

	public int getCharacteristicId() {
		return characteristicId;
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