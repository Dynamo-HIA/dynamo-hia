package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Skewness extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "skewness";

	public Skewness() {
		super(XMLElementName, new Float(0), Float.MAX_VALUE);
	}
}
