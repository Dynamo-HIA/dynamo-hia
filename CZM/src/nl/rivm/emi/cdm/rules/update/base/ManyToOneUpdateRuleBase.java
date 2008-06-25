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
	 * Override, this is just a very finicky example.
	 * 
	 * @param currentValues
	 * @return The Result when AOK, null when a parameter is missing, a
	 *         ConfigurationException when the types of the parameters do not
	 *         match.
	 * @throws CDMUpdateRuleException 
	 */
	public Object update(Object[] currentValues) throws CDMUpdateRuleException {
		Object justAnObject = new Object();
		for (Object inputObj : currentValues) {
			if (inputObj == null) {
				justAnObject = null;
				break;
			}
		}
		return justAnObject;
	}
}
