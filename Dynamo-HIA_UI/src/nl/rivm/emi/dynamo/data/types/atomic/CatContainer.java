package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/*
 * Nonnegative Integer without fixed upper limit.
 */
public class CatContainer extends AbstractClassIndex implements ContainerType{
	static final protected String XMLElementName = "cat";

	public CatContainer(){
		super(XMLElementName , new Integer(0), new Integer(9));
	}

	public Integer getDefaultValue() {
		return new Integer(0);
	}
}
