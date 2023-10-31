package nl.rivm.emi.cdm.rules.update;

import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;

/**
 * @deprecated Class cannot be used as intended via ClassLoading, so it cannot
 *             be usefull for the application.
 *             
 * @author mondeelr
 * 
 */
public abstract class AbstractDoubleBoundOneToOneUpdateRule implements
		UpdateRuleMarker, CharacteristicSpecific, StepSizeSpecific {

	private int characteristicId;

	private float stepSize;

	protected AbstractDoubleBoundOneToOneUpdateRule() {
		super();
	}

	protected AbstractDoubleBoundOneToOneUpdateRule(int characteristicId,
			float stepSize) {
		super();
		this.characteristicId = characteristicId;
		this.stepSize = stepSize;
	}

	abstract public Object update(Object currentValue);
}
