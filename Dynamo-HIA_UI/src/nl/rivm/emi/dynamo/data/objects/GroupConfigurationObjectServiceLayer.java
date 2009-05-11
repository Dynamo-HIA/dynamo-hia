package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class GroupConfigurationObjectServiceLayer extends
		LinkedHashMap<String, Object> {

	public GroupConfigurationObjectServiceLayer() {
		super();
	}

	private GroupConfigurationObjectServiceLayer(int arg0) {
		super(arg0);
	}

	private GroupConfigurationObjectServiceLayer(Map arg0) {
		super(arg0);
	}

	private GroupConfigurationObjectServiceLayer(int arg0, float arg1) {
		super(arg0, arg1);
	}

	private GroupConfigurationObjectServiceLayer(int arg0, float arg1,
			boolean arg2) {
		super(arg0, arg1, arg2);
	}

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

	protected String getSingleRootChildStringValue(String rootChildName) {
		Object flagObject = get(rootChildName);
		String flag = null;
		if (flagObject instanceof WritableValue) {
			flag = (String) ((WritableValue) flagObject).doGetValue();
		} else {
			flag = (String) flagObject;
		}
		return flag;
	}

	protected WritableValue getSingleRootChildWritableValue(String rootChildName) {
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) get(rootChildName);
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