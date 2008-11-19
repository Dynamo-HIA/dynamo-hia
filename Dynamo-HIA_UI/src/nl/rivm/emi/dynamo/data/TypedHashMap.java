package nl.rivm.emi.dynamo.data;

import java.util.LinkedHashMap;

public class TypedHashMap<T> extends LinkedHashMap<Integer, Object> {
	private static final long serialVersionUID = 1345063403320022388L;
	T type = null;

	/**
	 * Block untyped use.
	 */
	@SuppressWarnings("unused")
	private TypedHashMap(){
	
	}
	
	public TypedHashMap(T theType) {
		super();
		type = theType;
	}

	public T getType() {
		return type;
	}
}
