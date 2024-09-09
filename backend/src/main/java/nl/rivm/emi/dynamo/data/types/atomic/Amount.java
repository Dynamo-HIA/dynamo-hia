package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class Amount extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "amount";

	public Amount(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
	}
}
