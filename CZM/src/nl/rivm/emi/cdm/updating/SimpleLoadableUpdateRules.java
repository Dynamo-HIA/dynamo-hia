package nl.rivm.emi.cdm.updating;

public class SimpleLoadableUpdateRules extends UpdateRuleBaseClass {

	public SimpleLoadableUpdateRules(int characteristicId, int stepSize){
		super(characteristicId, stepSize);
	}
	public int updateOneToOneSquared(int input){
		int result = input;
		result = result*result;
		return result;
	}
	@Override
	public int updateSelf(int currentValue) {
		// TODO Auto-generated method stub
		return 0;
	}
}
