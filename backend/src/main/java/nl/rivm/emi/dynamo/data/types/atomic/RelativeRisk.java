package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class RelativeRisk extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "relativerisk";

	public RelativeRisk(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
	}
}
