package nl.rivm.emi.dynamo.data;

/**
 * LinkedHashMap extension to store containertypes in.
 * 
 * 20090330 mondeelr Effectively killed generics by changing it from 
 * <Integer,Object> on the LinkedHashMap to <Object,Object> to allow for String keys. 
 * 
 */
import java.util.LinkedHashMap;

public class TypedHashMap<T> extends LinkedHashMap<Object, Object> {
	private static final long serialVersionUID = 1345063403320022388L;
	T type = null;

	/**
	 * Block untyped use.
	 */
	@SuppressWarnings("unused")
	public TypedHashMap() {

	}

	public TypedHashMap(T theType) {
		super();
		type = theType;
	}

	public T getType() {
		return type;
	}
}
