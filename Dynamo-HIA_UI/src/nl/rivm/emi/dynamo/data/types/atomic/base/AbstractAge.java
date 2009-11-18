package nl.rivm.emi.dynamo.data.types.atomic.base;

/**
 * 
 * @author mondeelr <br/>
 *         Specialisation for the support of ages.<br/>
 *         Note that the default behavior is tailored to the Dynamo-HIA
 *         specification: Integer ages between 0 and 95 inclusive.
 */
abstract public class AbstractAge extends AbstractRangedInteger {
	/**
	 * Default minimum agevalue.
	 */
	public static final Integer MINAGE = 0;
	/**
	 * Default maximum agevalue.
	 */
	public static final Integer MAXAGE = 95;

	/**
	 * Constructor that binds the elementname and sets the default age-limits.
	 * 
	 * @param xmlElementName
	 *            The name of the supported elementname.
	 */
	public AbstractAge(String xmlElementName) {
		super(xmlElementName, AbstractAge.MINAGE, AbstractAge.MAXAGE);
	}

	/**
	 * Constructor that allows the setting of custom age-limits.
	 * 
	 * @param xmlElementName
	 *            The name of the supported elementname.
	 * @param minAge
	 *            Custom lower age limit.
	 * @param maxAge
	 *            Custom upper age limit.
	 */
	protected AbstractAge(String xmlElementName, Integer minAge, Integer maxAge) {
		super(xmlElementName, minAge, maxAge);
	}
}
