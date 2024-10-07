package nl.rivm.emi.dynamo.data.util;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

/**
 * @author mondeelr<br/>
 * 
 * Container bean for keeping a value and its atomic type together for use in a configuration Object.
 */
public class AtomicTypeObjectTuple {
	/**
	 *  The type of the value.
	 */
	XMLTagEntity type;
	/**
	 *  The value.
	 */
	Object value;

	/**
	 * @param tagEntity The type of the value.
	 * @param value The value.
	 */
	public AtomicTypeObjectTuple(XMLTagEntity tagEntity, Object value) {
		this.type = tagEntity;
		this.value = value;
	}

	/**
	 * @return The type of the value
	 */
	public XMLTagEntity getType() {
		return type;
	}

	/**
	 * @return The value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value The value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
