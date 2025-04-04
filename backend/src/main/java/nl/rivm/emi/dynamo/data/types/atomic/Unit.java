package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Unit extends AbstractValue implements PayloadType<Float>{

	static final protected String XMLElementName = "unit";

	/**
	 * Constructor
	 */
	public Unit(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}

}
