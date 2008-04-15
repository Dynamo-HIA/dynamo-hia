package nl.rivm.emi.cdm.updaterules.containment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.cdm.updaterules.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.updaterules.base.UpdateRuleMarker;

public class UpdateRulesByCharIdRepository extends
		HashMap<Integer, Set<UpdateRuleMarker>> {

	private static final long serialVersionUID = 7757293271866547455L;
	
	Set<UpdateRuleMarker> unboundUpdateRuleContainer = new HashSet<UpdateRuleMarker>();

	public UpdateRulesByCharIdRepository() {
		super();
	}

	/**
	 * 
	 * @param updateRule
	 * @return null if the slot was empty, an updateRule if one was replaced.
	 */
	public UpdateRuleMarker putUpdateRule(UpdateRuleMarker updateRule) {
		UpdateRuleMarker priorRule = null;
		Class[] klaasjes = updateRule.getClass().getInterfaces();
		boolean found = false;
		for (Class klaas : klaasjes) {
			if ("CharacteristicSpecific".equals(klaas.getName())) {
				found = true;
			}
		}
		if (found) {
			Integer characteristicIndex = ((CharacteristicSpecific) updateRule)
					.getCharacteristicId();
			Set<UpdateRuleMarker> priorRules = this.get(characteristicIndex);
			if(priorRules == null){
				priorRules = new HashSet<UpdateRuleMarker>();
				this.put(characteristicIndex, priorRules);
			}
			priorRules.add(updateRule);
		} else {
			unboundUpdateRuleContainer.add(updateRule);
		}
		return priorRule;
	}

	/**
	 * 
	 * @param characteristicId
	 * @return An updateRule for the given CharacteristicId, null if the slot
	 *         was empty.
	 */
	public Set<UpdateRuleMarker> getUpdateRuleSet(Integer characteristicId) {
		Set<UpdateRuleMarker> theRules = null;
		if(characteristicId != null){
		Integer characteristicIndex = characteristicId;
		 theRules = this.get(characteristicIndex);
		} else {
		theRules = this.unboundUpdateRuleContainer;	
		}
		return theRules;
	}

}
