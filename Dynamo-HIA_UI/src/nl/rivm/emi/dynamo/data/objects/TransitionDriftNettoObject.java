package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.value.WritableValue;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITrend;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Trend;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

/**
 * Object to contain the data entered in W21. The Observable contains a
 * nonegative Float with eight decimals.
 */

public class TransitionDriftNettoObject extends
		ArrayList<AtomicTypeObjectTuple> implements ITrend,
		StandardObjectMarker {
	private static final long serialVersionUID = -1973812253427654652L;

	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	public TransitionDriftNettoObject() {
		super();
	}

	public WritableValue getObservableTrend() throws DynamoConfigurationException {
		WritableValue result = null;
		Object candidateValue = getValue();
		if (candidateValue instanceof WritableValue) {
			result = (WritableValue) candidateValue;
		}
		return result;
	}

	public Float getTrend() throws DynamoConfigurationException {
		Float result = null;
		Object candidateValue = getValue();
		if (candidateValue instanceof WritableValue) {
			Object candidateValue2 = ((WritableValue) candidateValue)
					.doGetValue();
			if (candidateValue2 instanceof Float) {
				result = (Float) candidateValue2;
			}
		} else {
			if (candidateValue instanceof Float) {
				result = (Float) candidateValue;
			}
		}
		return result;
	}

	public void setTrend(Float trend) throws DynamoConfigurationException {
		Object currentValue = getValue();
		if (currentValue != null) {
			if (currentValue instanceof WritableValue) {
				WritableValue newValue = new WritableValue(trend,
						((WritableValue) currentValue).getValueType());
				setValue(newValue);
			} else {
				if (currentValue instanceof Float) {
					setValue(trend);
				} else {
					throw new DynamoConfigurationException(
							"Unexpected currentValue Object type: "
									+ currentValue.getClass().getName());
				}
			}
		} else {
			// Constructing a new Object.
			setValue(trend);
		}
	}

	public void setObservableTrend(WritableValue trend)
			throws DynamoConfigurationException {
		setValue(trend);
	}

	private Object getValue() throws DynamoConfigurationException {
		Object value = null;
		if (size() > 0) {
			AtomicTypeObjectTuple tuple = get(0);
			value = tuple.getValue();
		} else {
			throw new DynamoConfigurationException(
			"Can't get the value from an empty Object.");
}
		return value;
	}

	private void setValue(Object value) throws DynamoConfigurationException {
		if (size() == 1) {
			AtomicTypeObjectTuple tuple = get(0);
			tuple.setValue(value);
		} else {
			if(size() == 0){
			AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TREND.getTheType(), value);
			add(0, tuple);
			} else {
				throw new DynamoConfigurationException(
				"Unexpected Object size: " + size());
			}
		}
	}
}
