package nl.rivm.emi.dynamo.data.types.atomic.base;

abstract public class AbstractAge extends AbstractRangedInteger {

	public static final Integer MINAGE = 0;
	public static final Integer MAXAGE = 95;

	public AbstractAge(String xmlElementName) {
		super(xmlElementName, AbstractAge.MINAGE, AbstractAge.MAXAGE);
	}
}
