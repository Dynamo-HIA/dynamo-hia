package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class RR extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "RR";

	public RR() {
		super(XMLElementName);
	}

	/**
	 * This type wraps no other wrappers.
	 */
	public WrapperType getNextWrapper() {
		return null;
	}
}
