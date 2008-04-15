package nl.rivm.emi.cdm.updaterules;

import nl.rivm.emi.cdm.updaterules.base.OneToOneUpdateRuleBase;


abstract public class IntegerUnboundOneToOneUpdateRule extends OneToOneUpdateRuleBase{

	protected IntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	abstract public Integer update(Object currentValue);
}
