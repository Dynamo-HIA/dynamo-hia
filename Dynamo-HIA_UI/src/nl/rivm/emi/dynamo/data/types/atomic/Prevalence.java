package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class Prevalence extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "prevalence";

	public Prevalence(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
	}
}
