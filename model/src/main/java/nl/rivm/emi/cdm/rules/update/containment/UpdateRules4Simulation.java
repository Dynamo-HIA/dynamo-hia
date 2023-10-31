package nl.rivm.emi.cdm.rules.update.containment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;

public class UpdateRules4Simulation extends HashMap<Integer, UpdateRuleMarker> {

	public UpdateRules4Simulation() {
		super();
	}

	/**
	 * @param updateRule
	 * @return null if the slot was empty, an updateRule if one was replaced.
	 * @throws CDMConfigurationException
	 */
	public UpdateRuleMarker putUpdateRule(Integer characteristicId,
			UpdateRuleMarker updateRule) throws CDMConfigurationException {
		UpdateRuleMarker priorRule = null;
		Class[] klaasjes = updateRule.getClass().getInterfaces();
		boolean found = false;
		for (Class klaas : klaasjes) {
			if ("CharacteristicSpecific".equals(klaas.getName())) {
				found = true;
			}
		}
		if (found) {
			Integer updateRuleCharacteristicIndex = ((CharacteristicSpecific) updateRule)
					.getCharacteristicId();
			if (characteristicId == updateRuleCharacteristicIndex) {
				priorRule = this.get(updateRuleCharacteristicIndex);
				put(characteristicId, updateRule);
			} else {
				throw new CDMConfigurationException(
						String
								.format(
										"Update rule %1$s cannot be used for characteristic %2$d.",
										updateRule.getClass().getSimpleName(),
										CharacteristicsConfigurationMapSingleton
												.getInstance().get(
														characteristicId)
												.getLabel()));
			}
		} else {
			priorRule = this.get(characteristicId);
			put(characteristicId, updateRule);
		}
		return priorRule;
	}

	/**
	 * 
	 * @param characteristicId
	 * @return An updateRule for the given CharacteristicId, null if the slot
	 *         was empty.
	 */
	public UpdateRuleMarker getUpdateRule(Integer characteristicId) {
		UpdateRuleMarker daRule = null;
		if (characteristicId != null) {
			daRule = this.get(characteristicId);
		}
		return daRule;
	}

}
