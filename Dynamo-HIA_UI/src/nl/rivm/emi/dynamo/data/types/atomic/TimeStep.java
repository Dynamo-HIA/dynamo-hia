package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class TimeStep extends AbstractRangedInteger implements PayloadType<Integer> {
	static final protected String XMLElementName = "timeStep";

	/**
	 * Currently limited to the value 1.
	 */
	public TimeStep() {
		super(XMLElementName, 1, 1);
	}

	@Override
	public Integer getDefaultValue() {
		return 1;
	}

}
