package nl.rivm.emi.dynamo.data.util;

import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;

public class AtomicTypeObjectTuple {
	XMLTagEntity type;
	Object value;

	public AtomicTypeObjectTuple(XMLTagEntity tagEntity, Object value) {
		this.type = tagEntity;
		this.value = value;
	}

	public XMLTagEntity getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
