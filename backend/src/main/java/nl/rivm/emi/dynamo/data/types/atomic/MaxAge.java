package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractAge;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class MaxAge extends AbstractAge implements PayloadType<Integer> {
	static final protected String XMLElementName = "maxAge";

	public MaxAge() {
		super(XMLElementName, new Integer(0), AbstractAge.MAXAGE);
	}
	
	@Override
	public Integer getDefaultValue() {
		return 95;
	}
}
