package nl.rivm.emi.dynamo.data.util;

import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;

public class AtomicTypeObjectTuple {
	AtomicTypeBase type;
	Object value;

	public AtomicTypeObjectTuple(AtomicTypeBase theType, Object value) {
		this.type = theType;
		this.value = value;
	}

	public AtomicTypeBase getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
