package nl.rivm.emi.dynamo.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr<br/>
 *         LinkedHashMap extension to store
 *         {@link nl.rivm.emi.dynamo.data.types.interfaces.ContainerType<T>} in.<br/>
 * 
 *         20090330 mondeelr Effectively killed ( :-) generics by changing it
 *         from <Integer,Object> on the LinkedHashMap to <Object,Object> to
 *         allow for String keys.
 * 
 */
public class TypedHashMap<T> extends LinkedHashMap<Object, Object> {
	private static final long serialVersionUID = 1345063403320022388L;
	Log log = LogFactory.getLog(this.getClass().getName());
	T type = null;

	/**
	 * Private default constructor to block untyped use.
	 */
	@SuppressWarnings("unused")
	private TypedHashMap() {

	}

	/**
	 * Constructor for creating an empty Map.
	 * 
	 * @param theType
	 *            The type of the values that are going to be put in the Map.
	 */
	public TypedHashMap(T theType) {
		super();
		type = theType;
	}

	/**
	 * Copy constructor, instantiates a new Map an fills it with the content of
	 * the parameter.
	 * 
	 * @param map
	 *            TypedHashMap to be copied.
	 * @throws DynamoConfigurationException
	 */
	public TypedHashMap(TypedHashMap<T> map)
			throws DynamoConfigurationException {
		this(map.getType());
		if (type != null) {
			Set<Object> keySet = map.keySet();
			for (Object key : keySet) {
				Object value = map.get(key);
				if (value instanceof TypedHashMap<?>) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					TypedHashMap nextLevelTypedHashMap = new TypedHashMap(
							(TypedHashMap<?>) value);
					put(key, nextLevelTypedHashMap);
				} else {
					ArrayList<AtomicTypeObjectTuple> newList = deepCopyEnclosedArrayList(value);
					put(key, newList);
				}
			} // for ends.
		} else {
			log
					.fatal("TypedHashMap(map) failed, the type of the map to be copies is null.");
			throw new DynamoConfigurationException(
					"Fatal TypedHashMap(map) failure, see logging entries.");
		}
	}

	/**
	 * Returns the type of the data contained in the Map.
	 * 
	 * @return
	 */
	public T getType() {
		return type;
	}

	/**
	 * Clones contained ArrayLists.
	 * 
	 * @param value
	 *            ArrayList to copy.
	 * @return The copy of the passed ArrayList.
	 * @throws DynamoConfigurationException
	 *             Thrown when unsupported Types are encountered.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<AtomicTypeObjectTuple> deepCopyEnclosedArrayList(
			Object value) throws DynamoConfigurationException {
		ArrayList<AtomicTypeObjectTuple> newList = null;
		if (value instanceof ArrayList) {
			newList = new ArrayList<AtomicTypeObjectTuple>();
			for (AtomicTypeObjectTuple oldTuple : (ArrayList<AtomicTypeObjectTuple>) value) {
				Object oldTupleValue = oldTuple.getValue();
				XMLTagEntity oldTupleType = oldTuple.getType();
				
				WritableValue newTupleValue = null;
				if (oldTupleValue instanceof WritableValue) {
				
					Object oldWritableValue = ((WritableValue) oldTupleValue)
							.getValue();
					
					Object oldWritableType = ((WritableValue) oldTupleValue)
							.getValueType();
					if (oldWritableValue instanceof Float) {
						// Float doesn't clone #@%&.
						Object newWritableValue = Float.valueOf(
								((Float) oldWritableValue).floatValue());
						newTupleValue = new WritableValue(newWritableValue,
								oldWritableType);
						AtomicTypeObjectTuple newTuple = new AtomicTypeObjectTuple(
								oldTupleType, newTupleValue);
						newList.add(newTuple);
					} else {
						log
								.fatal("handleEnclosedArrayList() only supports Float for now.");
						throw new DynamoConfigurationException(
								"Fatal handleEnclosedArrayList() failure, it only supports Float for now.");
					}
				} else {
					log
							.fatal("handleEnclosedArrayList() only supports WritableValues for now.");
					throw new DynamoConfigurationException(
							"Fatal handleEnclosedArrayList() failure, it only supports WritableValues for now.");
				}
			}
		} else {
			log.fatal("handleEnclosedArrayList() failed, unexpected Class: "
					+ value.getClass().getName());
			throw new DynamoConfigurationException(
					"Fatal handleEnclosedArrayList() failure, it encountered an unexpected Class: "
							+ value.getClass().getName());
		}
		return newList;
	}
}
