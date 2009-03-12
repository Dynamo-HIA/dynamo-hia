package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;


public abstract class TimeStepType extends AbstractValue {
	static final protected String XMLElementName = "timestep";

	public TimeStepType() throws ConfigurationException {
		super(XMLElementName, 1F, 1F);
	}
}
