package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
public class Diseases extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "diseases";

	public Diseases() {
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return (WrapperType)XMLTagEntityEnum.DISEASE.getTheType();
	}
}
