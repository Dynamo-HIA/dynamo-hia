package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class SimPopSize extends AbstractRangedInteger implements PayloadType<Integer>{
	static final protected String XMLElementName = "simPopSize";

	public SimPopSize(){
		super(XMLElementName, 1, Integer.MAX_VALUE);
	}
	@Override
	public Integer getDefaultValue() {
		return 10;
	}
}
