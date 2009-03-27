package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFileName;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class ExessMortFileName extends AbstractFileName implements PayloadType<String>{
	static final protected String XMLElementName = "excessmortfilename";

	public ExessMortFileName(){
		super(XMLElementName);
	}
}
