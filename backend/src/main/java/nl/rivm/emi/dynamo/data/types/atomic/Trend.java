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
		// veranderd door hendriek februari 2015 Float.MIN_Value is getal dichts bij 0, niet meest negative getal
		super(XMLElementName, - Float.MAX_VALUE, Float.MAX_VALUE);
	}	
}

