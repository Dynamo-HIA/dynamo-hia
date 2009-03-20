package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Percent extends AbstractValue implements
		PayloadType<Float> {
	static final protected String XMLElementName = "percent";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	// NB(mondeelr) This pattern must not be discarded.
	final public Pattern matchPattern = Pattern
			.compile("^\\d{0,3}(\\.\\d{0,8}?)?$");

	public Percent(){
		super("percent", new Float(0), new Float(100));
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

}
