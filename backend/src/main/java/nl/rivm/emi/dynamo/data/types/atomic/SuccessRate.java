package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class SuccessRate extends AbstractRangedInteger implements PayloadType<Integer> {
	static final protected String XMLElementName = "successRate";

	public SuccessRate() {
		super(XMLElementName, 0, 100);
	}

	public Integer getDefaultValue() {
		return Integer.valueOf(100);
	}


}
