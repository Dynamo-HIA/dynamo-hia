package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class GroupConfigurationObjectServiceLayer extends
		LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 7507565290488147794L;

/**
 * Overrides the default constructor of the superclass.
 * Just calls the constructor of the superclass.
 */
	public GroupConfigurationObjectServiceLayer() {
		super();
	}

	/**
	 * Blocks the constructor of the superclass.
	 */
	@SuppressWarnings("unused")
	private GroupConfigurationObjectServiceLayer(int arg0) {
		super(arg0);
	}

	/**
	 * Blocks the constructor of the superclass.
	 */
	@SuppressWarnings("unused")
	private GroupConfigurationObjectServiceLayer(Map<String,?> arg0) {
		super(arg0);
	}

	/**
	 * Blocks the constructor of the superclass.
	 */
	@SuppressWarnings("unused")
	private GroupConfigurationObjectServiceLayer(int arg0, float arg1) {
		super(arg0, arg1);
	}

	/**
	 * Blocks the constructor of the superclass.
	 */
	@SuppressWarnings("unused")
	private GroupConfigurationObjectServiceLayer(int arg0, float arg1,
			boolean arg2) {
		super(arg0, arg1, arg2);
	}

	@SuppressWarnings("rawtypes")
	protected Integer getSingleRootChildIntegerValue(String rootChildName) {
		Object classIndexObject = get(rootChildName);
		if (classIndexObject instanceof AtomicTypeObjectTuple) {
			classIndexObject = ((AtomicTypeObjectTuple) classIndexObject)
					.getValue();
		}

		Integer classIndex = null;
		if (classIndexObject instanceof WritableValue) {
			classIndex = (Integer) ((WritableValue) classIndexObject)
					.doGetValue();
		} else {
			classIndex = (Integer) classIndexObject;
		}
		return classIndex;
	}

	@SuppressWarnings("rawtypes")
	protected Float getSingleRootChildFloatValue(String rootChildName) {
		Object floatValueObject = get(rootChildName);
		Float floatValue = null;
		if (floatValueObject instanceof WritableValue) {
			floatValue = (Float) ((WritableValue) floatValueObject)
					.doGetValue();
		} else {
			floatValue = (Float) floatValueObject;
		}
		return floatValue;
	}

	@SuppressWarnings("rawtypes")
	protected Boolean getSingleRootChildBooleanValue(String rootChildName) {
		Object flagObject = get(rootChildName);
		Boolean flag = null;
		if (flagObject instanceof WritableValue) {
			flag = (Boolean) ((WritableValue) flagObject).doGetValue();
		} else {
			flag = (Boolean) flagObject;
		}
		return flag;
	}

	@SuppressWarnings("rawtypes")
	protected String getSingleRootChildStringValue(String rootChildName) {
		Object flagObject = get(rootChildName);
		String flag = null;
		if (flagObject instanceof WritableValue) {
			flag = (String) ((WritableValue) flagObject).doGetValue();
		} else {
			// 20100409
			if(flagObject instanceof AtomicTypeObjectTuple){
				AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple)flagObject;
				flag = (String) ((WritableValue)tuple.getValue()).doGetValue();
			}else {
			flag = (String) flagObject;
			}
		}
		return flag;
	}

	@SuppressWarnings("rawtypes")
	protected WritableValue getSingleRootChildWritableValue(String rootChildName) throws DynamoConfigurationException {
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) get(rootChildName);
		if (tuple==null) throw new DynamoConfigurationException("no tag: "+rootChildName+" in XML file" );
		Object classIndexObject = tuple.getValue();
		
		WritableValue result = null;
		if (classIndexObject instanceof WritableValue) {
			result = (WritableValue) classIndexObject;
		}
		return result;
	}

	protected Object putSingleRootChildIntegerValue(String rootChildName,
			Integer index) {
		return putSingleRootChildObjectValue(rootChildName, index);
	}

	protected Object putSingleRootChildFloatValue(String rootChildName,
			Float value) {
		return putSingleRootChildObjectValue(rootChildName, value);
	}

	protected Object putSingleRootChildBooleanValue(String rootChildName,
			Boolean flag) {
		return putSingleRootChildObjectValue(rootChildName, flag);
	}

	protected Object putSingleRootChildStringValue(String rootChildName,
			String value) {
		return putSingleRootChildObjectValue(rootChildName, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object putSingleRootChildObjectValue(String rootChildName,
			Object value) {
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) get(rootChildName);
		Object classIndexObject = tuple.getValue();
		if (classIndexObject instanceof WritableValue) {
			((WritableValue) classIndexObject).doSetValue(value);
		} else {
			classIndexObject = value;
		}
		tuple.setValue(classIndexObject);
		put(rootChildName, tuple);
		return null;
	}
}