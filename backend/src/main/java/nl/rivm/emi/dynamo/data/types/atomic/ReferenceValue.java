package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class ReferenceValue extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "referencevalue";

	public ReferenceValue() {
		super(XMLElementName, Float.valueOf(0), Float.MAX_VALUE);
	}
	
}
