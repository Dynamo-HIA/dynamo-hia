package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFlexibleUpperLimitInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class Duration extends AbstractFlexibleUpperLimitInteger implements ContainerType<Integer>{
	static final protected String XMLElementName = "duration";

	public Duration(){
		super(XMLElementName , Integer.valueOf(1),  Integer.valueOf(20));
	}
}
