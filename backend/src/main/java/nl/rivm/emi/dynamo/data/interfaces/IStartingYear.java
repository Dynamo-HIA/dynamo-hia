package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IStartingYear {

	public abstract Integer getStartingYear();

	@SuppressWarnings("rawtypes")
	public abstract WritableValue getObservableStartingYear() throws DynamoConfigurationException;

	public abstract void setStartingYear(Integer startingYear);

}