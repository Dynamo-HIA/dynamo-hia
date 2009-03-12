package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

public class StartingYear extends AbstractRangedInteger {
	static final protected String XMLElementName = "startingyear";

	public StartingYear() throws ConfigurationException {
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
}
