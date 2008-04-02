package nl.rivm.emi.cdm.updaterules;

import java.util.HashMap;
import java.util.Set;

public class UpdateRuleStorage extends
		HashMap<Float, UpdateRulesByCharIdContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 52692762374033733L;
	
	UpdateRulesByCharIdContainer noStepSizeContainer = new UpdateRulesByCharIdContainer();

	public UpdateRuleStorage() {
		super();
	}

	/**
	 * 
	 * @param container
	 * @return null if the slot was empty, an updateRulesContainer if one was
	 *         replaced.
	 */
	public UpdateRuleMarker addUpdateRule(UpdateRuleMarker updateRule) {
		UpdateRulesByCharIdContainer selectedContainer;
		Class[] klaasjes = updateRule.getClass().getInterfaces();
		boolean found = false;
		for(Class klaas: klaasjes){
			if("StepSizeSpecific".equals(klaas.getName())){
				found = true;
			}
		}
		if(found){
		Float stepSizeKey = new Float(((StepSizeSpecific)updateRule).getStepSize());
		selectedContainer = this.get(stepSizeKey);
		if (selectedContainer == null) {
			selectedContainer = new UpdateRulesByCharIdContainer();
			put(stepSizeKey, selectedContainer);
		}
		} else {
			selectedContainer = noStepSizeContainer;
		}
		UpdateRuleMarker priorUpdateRule = selectedContainer
				.putUpdateRule(updateRule);
		return priorUpdateRule;
	}

	/**
	 * 
	 * @param container
	 * @return null if the slot was empty, an updateRulesContainer if one was
	 *         replaced.
	 */
	public Set<UpdateRuleMarker> getUpdateRules(Integer characteristicId, Float stepSize) {
		UpdateRulesByCharIdContainer priorContainer = this.get(stepSize);
		Set<UpdateRuleMarker> updateRules = null;
		if (priorContainer != null) {
			updateRules = priorContainer.getUpdateRuleSet(characteristicId);
		}
		return updateRules;
	}
}
