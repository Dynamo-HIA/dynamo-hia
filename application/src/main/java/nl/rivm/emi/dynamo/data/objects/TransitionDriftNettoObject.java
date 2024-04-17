package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.interfaces.ITrend;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;

import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * Object to contain the data entered in W21. The Observable contains a
 * nonegative Float with eight decimals.
 * 
 * 
 */

public class TransitionDriftNettoObject extends
		GroupConfigurationObjectServiceLayer implements ITrend,
		StandardObjectMarker {
	private static final long serialVersionUID = -1973812253427654652L;

	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	public TransitionDriftNettoObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

/**
 * Tested and in use for databinding in the modal.
 */
	public WritableValue getObservableTrend()
			throws DynamoConfigurationException {
		WritableValue result = null;
		Object candidateValue = getSingleRootChildWritableValue(XMLTagEntityEnum.TREND.getElementName());
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
			value = getSingleRootChildFloatValue(XMLTagEntityEnum.TREND
					.getTheType().getXMLElementName());
		} else {
			throw new DynamoConfigurationException(
					"Can't get the value from an empty Object.");
		}
		return value;
	}

	private void setValue(Object value) throws DynamoConfigurationException {
		if (value instanceof Float) {
			putSingleRootChildFloatValue(XMLTagEntityEnum.TREND.getTheType()
					.getXMLElementName(), (Float) value);
		} else {
			throw new DynamoConfigurationException("Unexpected Object type: "
					+ value.getClass().getSimpleName());
		}
	}
}
