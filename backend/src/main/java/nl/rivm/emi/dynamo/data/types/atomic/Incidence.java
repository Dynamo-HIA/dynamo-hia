package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class Incidence extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "incidence";

	public Incidence(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
	}
}
