package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class StandardValue extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "value";

	public StandardValue() {
		super(XMLElementName);
	}

	public StandardValue(String elementName, Float minimum, Float maximum) {
		super(elementName, minimum, maximum);
	}
}
