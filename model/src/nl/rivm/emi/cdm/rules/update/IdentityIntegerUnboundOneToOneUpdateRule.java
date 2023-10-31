package nl.rivm.emi.cdm.rules.update;

import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;


public class IdentityIntegerUnboundOneToOneUpdateRule extends OneToOneUpdateRuleBase{

	public IdentityIntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	public Integer update(Object currentValue){
		return (Integer)currentValue;
	}
}
