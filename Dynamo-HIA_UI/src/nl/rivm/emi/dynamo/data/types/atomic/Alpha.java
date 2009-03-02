package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Alpha extends Value implements PayloadType<Float>{
	static final protected String XMLElementName = "alpha";

	public Alpha(){
		super(XMLElementName, 0F, Float.MAX_VALUE);
	}
}
