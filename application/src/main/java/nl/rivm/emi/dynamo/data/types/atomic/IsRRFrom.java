package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class IsRRFrom extends AbstractString implements PayloadType<String>{
	static final protected String XMLElementName = "isRRfrom";
	public IsRRFrom(){
		super(XMLElementName);
	}
}
