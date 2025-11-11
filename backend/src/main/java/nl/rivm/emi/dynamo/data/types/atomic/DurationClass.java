package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;


public class DurationClass extends AbstractClassIndex implements PayloadType<Integer> {
	static final protected String XMLElementName = "durationclass";

	public DurationClass() {
		super(XMLElementName, Integer.valueOf(1), hardUpperLimit);
	}
}
