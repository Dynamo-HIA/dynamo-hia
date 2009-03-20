package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;


public class DurationClass extends AbstractClassIndex implements PayloadType<Integer> {
	static final protected String XMLElementName = "durationclass";

	public DurationClass() {
		super(XMLElementName, new Integer(1), hardUpperLimit);
	}

	public Integer getDefaultValue() {
			return new Integer(1);
	}
}
