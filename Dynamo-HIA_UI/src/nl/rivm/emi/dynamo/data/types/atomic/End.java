package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class End extends Value implements PayloadType<Float>{
	static final protected String XMLElementName = "end";

	public End(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}
}
