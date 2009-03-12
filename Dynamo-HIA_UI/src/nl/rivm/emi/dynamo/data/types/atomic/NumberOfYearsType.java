package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

public class NumberOfYearsType extends AbstractRangedInteger {
	static final protected String XMLElementName = "numberofyears";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 * @throws ConfigurationException 
	 */

	public NumberOfYearsType() throws ConfigurationException {
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
}
