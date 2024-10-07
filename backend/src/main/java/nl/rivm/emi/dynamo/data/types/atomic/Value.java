package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Value extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "value";

	public Value() {
		super(XMLElementName);
	}

	public Value(String elementName, Float minimum, Float maximum) {
		super(elementName, minimum, maximum);
	}
}
