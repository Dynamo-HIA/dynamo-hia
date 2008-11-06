package nl.rivm.emi.dynamo.data.containers;

import java.util.HashMap;
import java.util.Map;

public class SexMap<T> extends HashMap<Integer, T> {

	public SexMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SexMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		// TODO Auto-generated constructor stub
	}

	public SexMap(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public SexMap(Map<? extends Integer, ? extends T> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

}
