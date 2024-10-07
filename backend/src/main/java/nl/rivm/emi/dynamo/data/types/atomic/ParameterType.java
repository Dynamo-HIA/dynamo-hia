package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class ParameterType extends AbstractString implements PayloadType<String>{

	static final protected String XMLElementName = "parametertype";

	static public final String CHOOSE = "Choose parameter type";
	static public final String ACUTELY_FATAL = "Acutely Fatal";
	static public final String CURED_FRACTION = "Cured Fraction";

	public ParameterType(){
		super(XMLElementName);
	}

	@Override
	public String getDefaultValue() {
		return super.getDefaultValue();
	}
}
