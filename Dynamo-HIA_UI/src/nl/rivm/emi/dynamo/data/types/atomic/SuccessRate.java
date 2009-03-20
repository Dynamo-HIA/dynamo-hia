package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class SuccessRate extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "successrate";

	public SuccessRate() {
		super(XMLElementName);
	}

	public SuccessRate(String elementName, Float minimum, Float maximum) {
		super(elementName, minimum, maximum);
	}
}
