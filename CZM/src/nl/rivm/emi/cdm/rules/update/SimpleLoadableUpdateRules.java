package nl.rivm.emi.cdm.rules.update;

import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;

public class SimpleLoadableUpdateRules extends OneToOneUpdateRuleBase {

	public SimpleLoadableUpdateRules(){
		super();
	}
	public int updateOneToOneSquared(int input){
		int result = input;
		result = result*result;
		return result;
	}
	
	public int updateSelf(int currentValue) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object update(Object currentValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
