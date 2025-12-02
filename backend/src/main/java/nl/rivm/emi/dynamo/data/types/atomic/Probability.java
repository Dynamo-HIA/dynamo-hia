package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Probability extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "prob";

	public Probability(){
	super(XMLElementName, Float.valueOf(0F), Float.valueOf(1F));
	}
}
