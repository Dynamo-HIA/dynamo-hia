package nl.rivm.emi.cdm.updaterules;


abstract public class IntegerUnboundOneToOneUpdateRule extends AbstractUnboundOneToOneUpdateRule{

	protected IntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	abstract public Integer update(Object currentValue);
}
