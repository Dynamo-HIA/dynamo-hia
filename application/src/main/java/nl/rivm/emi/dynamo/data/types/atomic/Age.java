package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractAge;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class Age extends AbstractAge implements ContainerType {
	static final protected String XMLElementName = "age";
	public Age() {
		super(XMLElementName);
	}
}
