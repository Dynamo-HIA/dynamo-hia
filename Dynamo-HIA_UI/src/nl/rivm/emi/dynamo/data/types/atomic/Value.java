package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.configuration.ConfigurationException;

public class Value extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "value";

	public Value() {
		super(XMLElementName);
	}

	/**
	 * Constructor for use by subclasses.
	 * 
	 * @param elementName
	 * @param minimum
	 * @param maximum
	 * @throws ConfigurationException
	 */
	public Value(String elementName, Float minimum, Float maximum) {
		super(elementName, minimum, maximum);
	}
}
