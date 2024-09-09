package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class DynamoClass extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "class";

	public DynamoClass() {
		super(XMLElementName);
	}

	/**
	 * Class wrap no other wrappers.
	 */
	public WrapperType getNextWrapper() {
		return null;
	}
}
