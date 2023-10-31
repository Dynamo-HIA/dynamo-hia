package nl.rivm.emi.cdm.rules.update.containment;

import java.util.HashMap;
import java.util.Set;

import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;

public class UpdateRuleRepository extends
		HashMap<Float, UpdateRulesByCharIdRepository> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 52692762374033733L;
	
	UpdateRulesByCharIdRepository noStepSizeContainer = new UpdateRulesByCharIdRepository();

	public UpdateRuleRepository() {
		super();
	}

	/**
	 * 
	 * @param container
	 * @return null if the slot was empty, an updateRulesContainer if one was
	 *         replaced.
	 */
	public UpdateRuleMarker addUpdateRule(UpdateRuleMarker updateRule) {
		UpdateRulesByCharIdRepository selectedContainer;
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
			selectedContainer = new UpdateRulesByCharIdRepository();
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
		UpdateRulesByCharIdRepository priorContainer = this.get(stepSize);
		Set<UpdateRuleMarker> updateRules = null;
		if (priorContainer != null) {
			updateRules = priorContainer.getUpdateRuleSet(characteristicId);
		}
		return updateRules;
	}
}
