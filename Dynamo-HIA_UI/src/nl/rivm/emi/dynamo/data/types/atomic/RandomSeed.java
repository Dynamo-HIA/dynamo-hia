package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractValue;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class RandomSeed extends AbstractValue implements PayloadType<Float> {
	static final protected String XMLElementName = "randomSeed";

	public RandomSeed() {
		super(XMLElementName);
	}

	public RandomSeed(String elementName, Float minimum, Float maximum) {
		super(elementName, minimum, maximum);
	}
}
