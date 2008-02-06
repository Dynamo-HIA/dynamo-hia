package nl.rivm.emi.cdm.updating;

import java.util.HashMap;

public class UpdateRuleStorage extends
		HashMap<Integer, UpdateRulesByCharIdContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 52692762374033733L;

	public UpdateRuleStorage() {
		super();
	}

	/**
	 * 
	 * @param container
	 * @return null if the slot was empty, an updateRulesContainer if one was
	 *         replaced.
	 */
	public UpdateRuleBaseClass addUpdateRule(UpdateRuleBaseClass updateRule) {
		Integer stepSizeKey = new Integer(updateRule.getStepSize());
		UpdateRulesByCharIdContainer priorContainer = this.get(stepSizeKey);
		if (priorContainer == null) {
			priorContainer = new UpdateRulesByCharIdContainer();
			put(stepSizeKey, priorContainer);
		}
		UpdateRuleBaseClass priorUpdateRule = priorContainer
				.putUpdateRule(updateRule);
		return priorUpdateRule;
	}

	/**
	 * 
	 * @param container
	 * @return null if the slot was empty, an updateRulesContainer if one was
	 *         replaced.
	 */
	public UpdateRuleBaseClass getUpdateRule(int characteristicId, int stepSize) {
		Integer stepSizeKey = new Integer(stepSize);
		UpdateRulesByCharIdContainer priorContainer = this.get(stepSizeKey);
		UpdateRuleBaseClass updateRule = null;
		if (priorContainer != null) {
			updateRule = priorContainer.getUpdateRule(characteristicId);
		}
		return updateRule;
	}

}
