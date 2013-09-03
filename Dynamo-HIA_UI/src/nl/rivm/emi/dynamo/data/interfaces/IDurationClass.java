package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IDurationClass {

	public Object putDurationClass(Integer index);

	public Integer getDurationClass();

	public WritableValue getObservableDurationClass() throws DynamoConfigurationException;
}