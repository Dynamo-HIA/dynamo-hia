package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFileName;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class IncFileName extends AbstractFileName implements PayloadType<String>{
	static final protected String XMLElementName = "incfilename";

	public IncFileName(){
		super(XMLElementName);
	}
}
