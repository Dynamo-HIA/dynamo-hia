package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Trend extends AbstractValue implements PayloadType<Float>{

	static final protected String XMLElementName = "trend";
	
	public static int TREND_INDEX = 1;

	/**
	 * Constructor
	 */
	public Trend(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}	
}
