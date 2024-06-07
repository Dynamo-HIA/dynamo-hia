package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Pattern;

abstract public class AbstractPayLoadAge extends AbstractRangedInteger{

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern
			.compile("^\\d*$");

	public AbstractPayLoadAge(String XMLElementName){
		super(XMLElementName, AbstractAge.MINAGE, AbstractAge.MAXAGE);
	}

}
