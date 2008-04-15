package nl.rivm.emi.cdm.updaterules;

import nl.rivm.emi.cdm.updaterules.base.OneToOneUpdateRuleBase;


public class IdentityIntegerUnboundOneToOneUpdateRule extends OneToOneUpdateRuleBase{

	public IdentityIntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	public Integer update(Object currentValue){
		return (Integer)currentValue;
	}
}
