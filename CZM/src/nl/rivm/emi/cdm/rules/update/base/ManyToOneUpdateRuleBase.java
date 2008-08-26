package nl.rivm.emi.cdm.rules.update.base;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;


/**
 * 
 * @author mondeelr
 * 
 */
public abstract class ManyToOneUpdateRuleBase implements UpdateRuleMarker {

	protected ManyToOneUpdateRuleBase() {
		super();
	}

	/**
	 * @param currentValues
	 * @return The Result when AOK, null when a parameter is missing, a
	 *         ConfigurationException when the types of the parameters do not
	 *         match.
	 * @throws CDMUpdateRuleException 
	 */
	public abstract Object update(Object[] currentValues) throws CDMUpdateRuleException;
}
