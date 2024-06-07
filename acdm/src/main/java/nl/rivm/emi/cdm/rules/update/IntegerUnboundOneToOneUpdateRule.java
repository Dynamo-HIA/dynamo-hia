package nl.rivm.emi.cdm.rules.update;

import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;


abstract public class IntegerUnboundOneToOneUpdateRule extends OneToOneUpdateRuleBase{

	protected IntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	abstract public Integer update(Object currentValue);
}
