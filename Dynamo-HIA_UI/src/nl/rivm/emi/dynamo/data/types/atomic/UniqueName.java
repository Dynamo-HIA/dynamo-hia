package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class UniqueName extends AbstractString implements ContainerType{

	static final protected String XMLElementName = "uniquename";
	public UniqueName() {
		super(XMLElementName);
	}
}
