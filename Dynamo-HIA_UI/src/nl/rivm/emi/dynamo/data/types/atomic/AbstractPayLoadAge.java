package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

abstract public class AbstractPayLoadAge extends AbstractRangedInteger{

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\d*$");

	public AbstractPayLoadAge(String XMLElementName){
		super(XMLElementName, Age.MINAGE, Age.MAXAGE);
	}

}