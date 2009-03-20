package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class Disease extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "disease";

	public Disease() {
		super(XMLElementName);
	}

	/**
	 * Class wrap no other wrappers.
	 */
	public WrapperType getNextWrapper() {
		return null;
	}
}
