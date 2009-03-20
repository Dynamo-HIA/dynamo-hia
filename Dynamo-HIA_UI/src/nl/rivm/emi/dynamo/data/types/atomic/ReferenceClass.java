package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;


public class ReferenceClass extends AbstractClassIndex implements PayloadType<Integer>{
	static final protected String XMLElementName = "referenceclass";

	static final protected Integer hardUpperLimit = new Integer(9);

	public ReferenceClass(){
		super(XMLElementName, new Integer(1), hardUpperLimit);
	}
}
