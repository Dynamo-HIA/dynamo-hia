package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class NumberOfYears extends AbstractRangedInteger implements PayloadType<Integer>{
	static final protected String XMLElementName = "numberOfYears";

	public NumberOfYears(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}
	@Override
	public Integer getDefaultValue() {
		return 10;
	}
}
