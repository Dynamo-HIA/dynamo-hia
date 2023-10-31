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

	protected OneToOneUpdateRuleBase(int i, int y) {
		super();
	}
	
	abstract public Object update(Object currentValue) throws CDMUpdateRuleException ;

	public int update(int currentValue) {
		// TODO Auto-generated method stub 3-2-2009
		return 0;
	}

	public int updateSelf(int currentValue) {
		// TODO Auto-generated method stub
		return 0;
	}
}
