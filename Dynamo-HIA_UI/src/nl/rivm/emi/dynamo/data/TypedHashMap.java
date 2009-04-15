package nl.rivm.emi.dynamo.data;

/**
 * LinkedHashMap extension to store containertypes in.
 * 
 * 20090330 mondeelr Effectively killed ( :-) generics by changing it from 
 * <Integer,Object> on the LinkedHashMap to <Object,Object> to allow for String keys. 
 * 
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TypedHashMap<T> extends LinkedHashMap<Object, Object> {
	private static final long serialVersionUID = 1345063403320022388L;
	Log log = LogFactory.getLog(this.getClass().getName());
	T type = null;

	/**
	 * Block untyped use.
	 */
	@SuppressWarnings("unused")
	private TypedHashMap() {

	}

	public TypedHashMap(T theType) {
		super();
		type = theType;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param theType
	 * @throws DynamoConfigurationException
	 */
	public TypedHashMap(TypedHashMap<T> map)
			throws DynamoConfigurationException {
		super();
		type = map.getType();
		if (type != null) {
			Set<Object> keySet = map.keySet();
			for (Object key : keySet) {
				Object value = map.get(key);
				if (value instanceof TypedHashMap<?>) {
					TypedHashMap nextLevelTypedHashMap = new TypedHashMap(
							(TypedHashMap<?>) value);
					put(key, nextLevelTypedHashMap);
				} else {
					ArrayList<AtomicTypeObjectTuple> newList = handleEnclosedArrayList(value);
					put(key, newList);
				}
			} // for ends.
		} else {
			log.fatal("putAll() failed, one of the types was null.");
			throw new DynamoConfigurationException(
					"Fatal putAll() failure, see logging entries.");
		}
	}

	public T getType() {
		return type;
	}

	private ArrayList<AtomicTypeObjectTuple> handleEnclosedArrayList(
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
						Object newWritableValue = new Float(
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
