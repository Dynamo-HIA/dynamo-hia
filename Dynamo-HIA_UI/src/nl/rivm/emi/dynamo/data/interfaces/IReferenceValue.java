package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IReferenceValue {

	public Object putReferenceValue(Float value);

	public Float getReferenceValue();

	public WritableValue getObservableReferenceValue() throws DynamoConfigurationException;
}