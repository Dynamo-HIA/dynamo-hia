package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;


public class ReferenceClass extends AbstractClassIndex implements PayloadType<Integer>{
	static final protected String XMLElementName = "referenceclass";

	static final protected Integer hardUpperLimit =Integer.valueOf(10);

	public ReferenceClass(){
		super(XMLElementName, Integer.valueOf(1), hardUpperLimit);
	}
}
