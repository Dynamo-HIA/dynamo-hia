package nl.rivm.emi.cdm.updaterules;

/**
 * 
 * @author mondeelr
 *
 */
public abstract class AbstractUnboundOneToOneUpdateRule implements UpdateRuleMarker{

	protected AbstractUnboundOneToOneUpdateRule() {
		super();
	}

	abstract public Object update(Object currentValue);
}
