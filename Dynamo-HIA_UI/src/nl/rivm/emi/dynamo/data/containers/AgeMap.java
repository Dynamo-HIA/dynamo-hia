package nl.rivm.emi.dynamo.data.containers;

import java.util.HashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.types.Age;

public class AgeMap<T> extends HashMap<Integer, T> {

	public AgeMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AgeMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		// TODO Auto-generated constructor stub
	}

	public AgeMap(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public AgeMap(Map<? extends Integer, ? extends T> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public static String getXMLElementName() {
		return Age.XMLElementName;
	}
}
