package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractAge;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class TargetMaxAge extends AbstractAge implements PayloadType<Integer> {
	static final protected String XMLElementName = "targetMaxAge";

	public TargetMaxAge() {
		super(XMLElementName);
	}
	@Override
	public Integer getDefaultValue() {
		return 95;
	}
}
