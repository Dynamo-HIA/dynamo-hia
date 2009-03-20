package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class CatContainer extends AbstractClassIndex implements ContainerType{
	static final protected String XMLElementName = "cat";

	public CatContainer(){
		super(XMLElementName , new Integer(0), new Integer(9));
	}

	public Integer getDefaultValue() {
		return new Integer(0);
	}
}
