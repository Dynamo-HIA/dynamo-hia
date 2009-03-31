package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class RandomSeed extends AbstractRangedInteger implements PayloadType<Integer> {
	static final protected String XMLElementName = "randomSeed";

	public RandomSeed() {
		super(XMLElementName, 0 ,Integer.MAX_VALUE);
	}

	public Integer getDefaultValue() {
		return new Integer(1);
	}
}
