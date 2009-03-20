package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Alfa extends AbstractValue implements PayloadType<Float>{
	static final protected String XMLElementName = "alfa";

	public Alfa(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}
}
