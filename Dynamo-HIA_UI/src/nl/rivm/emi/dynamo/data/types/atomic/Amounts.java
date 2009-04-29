package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

/**
 */

public class Amounts extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "amounts";

	public Amounts() {
		super(XMLElementName);
	}
	
	public WrapperType getNextWrapper() {
		return (WrapperType)XMLTagEntitySingleton.getInstance().get("amount");
	}
}
