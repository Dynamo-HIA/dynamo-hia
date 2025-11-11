package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class TargetSex extends AbstractRangedInteger implements PayloadType<Integer>{
	static final protected String XMLElementName = "targetSex";

	public TargetSex(){
		super(XMLElementName, Integer.valueOf(0), Integer.valueOf(2));
	}
	
	public Integer getDefaultValue() {
		return Integer.valueOf(2);
	}
}
