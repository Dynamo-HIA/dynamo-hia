package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

@SuppressWarnings("rawtypes")
public class CatContainer extends AbstractClassIndex implements ContainerType{
	static final protected String XMLElementName = "cat";

	public CatContainer(){
		super(XMLElementName , Integer.valueOf(0), Integer.valueOf(9));
	}

	public Integer getDefaultValue() {
		return Integer.valueOf(0);
	}
}
