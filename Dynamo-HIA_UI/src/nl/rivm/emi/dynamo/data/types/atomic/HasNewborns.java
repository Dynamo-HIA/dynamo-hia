package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class HasNewborns extends AbstractBoolean implements PayloadType<Boolean>{
	static final protected String XMLElementName = "hasnewborns";

	public HasNewborns() {
		super(XMLElementName);
	}
}
