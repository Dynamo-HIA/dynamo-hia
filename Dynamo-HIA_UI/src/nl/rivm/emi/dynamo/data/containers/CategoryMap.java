package nl.rivm.emi.dynamo.data.containers;

import java.util.HashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class CategoryMap<T> extends HashMap<Integer, T> {

	public CategoryMap() {
		super();
	}

	public CategoryMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CategoryMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CategoryMap(Map<? extends Integer, ? extends T> m) {
		super(m);
	}

}
