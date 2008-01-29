package nl.rivm.emi.cdm.updating;

public class SimpleLoadableUpdateRules implements UpdateRulesBase {

	public Integer updateOneToOneSquared(Integer input){
		int result = input.intValue();
		result = result*result;
		return new Integer(result);
	}
}
