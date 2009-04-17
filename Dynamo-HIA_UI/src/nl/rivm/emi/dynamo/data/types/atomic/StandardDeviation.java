package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class StandardDeviation extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "standarddeviation";

	public StandardDeviation() {
		super(XMLElementName, new Float(0), Float.MAX_VALUE);
	}
}
