package nl.rivm.emi.cdm.updaterules.base;

import nl.rivm.emi.cdm.exceptions.WrongUpdateRuleException;


/**
 * 
 * @author mondeelr
 *
 */
public abstract class OneToOneUpdateRuleBase implements UpdateRuleMarker{

	protected OneToOneUpdateRuleBase() {
		super();
	}

	abstract public Object update(Object currentValue) throws WrongUpdateRuleException ;
}
