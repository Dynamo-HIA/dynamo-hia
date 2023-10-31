package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

/**
 */

public class AttributableMortality extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "attributableMortality";

	public AttributableMortality() {
		super(XMLElementName);
	}
	
	public WrapperType getNextWrapper() {
		return null;
	}
}
