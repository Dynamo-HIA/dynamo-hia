package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class TimeStep extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "timeStep";

	/**
	 * Currently limited to the value 1.
	 */
	public TimeStep() {
		super(XMLElementName, 1F, 1F);
	}
}
