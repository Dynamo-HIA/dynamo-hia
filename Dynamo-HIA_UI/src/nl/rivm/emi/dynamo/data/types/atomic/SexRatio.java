package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class SexRatio extends AbstractValue implements PayloadType<Float>{

	static final protected String XMLElementName = "sexratio";

	/**
	 * Constructor
	 */
	public SexRatio(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}
	
	@Override
	public Float getDefaultValue() {
		return 1.06F;
	}	
	
}
