package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IMaxAge {

	public abstract Integer getMaxAge();

	@SuppressWarnings("rawtypes")
	public abstract WritableValue getObservableMaxAge() throws DynamoConfigurationException;

	public abstract void setMaxAge(Integer maxAge);

}