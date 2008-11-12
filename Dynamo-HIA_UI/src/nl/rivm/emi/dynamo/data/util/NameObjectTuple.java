package nl.rivm.emi.dynamo.data.util;

public class NameObjectTuple {
	String name;
	Object value;

	public NameObjectTuple(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
