package nl.rivm.emi.cdm.rules.update.base;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;


/**
 * 
 * @author mondeelr
 *
 */
public abstract class OneToOneUpdateRuleBase implements UpdateRuleMarker{

	protected OneToOneUpdateRuleBase() {
		super();
	}

	abstract public Object update(Object currentValue) throws CDMUpdateRuleException ;
}
