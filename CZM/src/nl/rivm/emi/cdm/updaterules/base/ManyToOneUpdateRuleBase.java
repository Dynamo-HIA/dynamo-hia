package nl.rivm.emi.cdm.updaterules.base;


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
	 */
	public Object update(Object[] currentValues) {
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
