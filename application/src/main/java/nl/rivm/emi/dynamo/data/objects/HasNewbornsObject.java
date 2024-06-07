package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

public class HasNewbornsObject extends AtomicTypeObjectTuple{

	public HasNewbornsObject(AtomicTypeObjectTuple producedTuple) {
		super(producedTuple.getType(), producedTuple.getValue());
	}

}
