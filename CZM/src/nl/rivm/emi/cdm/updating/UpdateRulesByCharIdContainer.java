package nl.rivm.emi.cdm.updating;

import java.util.HashMap;

public class UpdateRulesByCharIdContainer extends HashMap<Integer, UpdateRuleBaseClass> {
	public UpdateRulesByCharIdContainer(){
		super();
	}
	
	/**
	 * 
	 * @param updateRule
	 * @return null if the slot was empty, an updateRule if one was replaced.
	 */
	public UpdateRuleBaseClass putUpdateRule(UpdateRuleBaseClass updateRule){
		Integer characteristicIndex = updateRule.getCharacteristicID();
		UpdateRuleBaseClass priorRule = this.get(characteristicIndex);
		this.put(characteristicIndex, updateRule);
		return priorRule;
	}

	/**
	 * 
	 * @param characteristicId
	 * @return An updateRule for the given CharacteristicId, null if the slot was empty.
	 */
	public UpdateRuleBaseClass getUpdateRule(int characteristicId){
		Integer characteristicIndex = characteristicId;
		UpdateRuleBaseClass theRule = this.get(characteristicIndex);
		return theRule;
	}

}
