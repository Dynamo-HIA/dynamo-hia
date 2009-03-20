package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Cutoff extends AbstractValue implements PayloadType<Float>{
	static final protected String XMLElementName = "cutoff";
	public Cutoff(){
		super(XMLElementName, new Float(0), Float.MAX_VALUE);
	}
}
