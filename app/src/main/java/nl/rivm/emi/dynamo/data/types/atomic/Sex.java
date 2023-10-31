package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

public class Sex extends AbstractRangedInteger implements ContainerType{
	static final protected String XMLElementName = "sex";

	public Sex(){
		super(XMLElementName, new Integer(0), new Integer(1));
	}
}
