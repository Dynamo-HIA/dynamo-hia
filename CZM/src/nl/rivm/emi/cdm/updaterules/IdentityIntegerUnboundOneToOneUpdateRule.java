package nl.rivm.emi.cdm.updaterules;


public class IdentityIntegerUnboundOneToOneUpdateRule extends AbstractUnboundOneToOneUpdateRule{

	public IdentityIntegerUnboundOneToOneUpdateRule() {
		super();
	}

	@Override
	public Integer update(Object currentValue){
		return (Integer)currentValue;
	}
}
