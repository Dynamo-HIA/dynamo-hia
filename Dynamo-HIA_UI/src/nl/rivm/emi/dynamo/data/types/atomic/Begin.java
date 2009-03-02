package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Begin extends Value implements PayloadType<Float>{
	static final protected String XMLElementName = "begin";

	public Begin(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}
}
