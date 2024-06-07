package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IMinAge {

	public abstract Integer getMinAge();

	public abstract WritableValue getObservableMinAge() throws DynamoConfigurationException;

	public abstract void setMinAge(Integer minAge);

}