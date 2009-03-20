package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFileName;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class PopFileName extends AbstractFileName implements PayloadType<String>{
	static final protected String XMLElementName = "popFileName";

	public PopFileName(){
		super(XMLElementName);
	}
}
