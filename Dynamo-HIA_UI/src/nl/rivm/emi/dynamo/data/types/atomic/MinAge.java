package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractAge;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class MinAge extends AbstractAge implements PayloadType<Integer> {
	static final protected String XMLElementName = "minAge";

	public MinAge() {
		super(XMLElementName);
	}
}
