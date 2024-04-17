package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Mean extends AbstractValue implements PayloadType<Float>{
	static final protected String XMLElementName = "mean";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern
			.compile("^-?\\d*\\.?\\d*$");
	

	public Mean(){
		super(XMLElementName, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
}
