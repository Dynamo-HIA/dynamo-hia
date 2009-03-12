package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

public abstract class SimPopSizeType extends AbstractRangedInteger {
	static final protected String XMLElementName = "simpopsize";

	public SimPopSizeType() throws ConfigurationException {
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
}
