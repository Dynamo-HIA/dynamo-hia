package nl.rivm.emi.dynamo.data.transition;

import java.util.HashMap;
import java.util.Map;

public class ValueByDestinationMap<T> extends HashMap<Integer, T> {

	public ValueByDestinationMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ValueByDestinationMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		// TODO Auto-generated constructor stub
	}

	public ValueByDestinationMap(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public ValueByDestinationMap(Map<? extends Integer, ? extends T> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

}
