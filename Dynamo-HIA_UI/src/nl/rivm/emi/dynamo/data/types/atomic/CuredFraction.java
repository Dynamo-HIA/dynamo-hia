package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class CuredFraction extends AbstractValue implements PayloadType<Float>{

	static final protected String XMLElementName = "curedfraction";

	/**
	 * Constructor
	 */
	public CuredFraction(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}

}
