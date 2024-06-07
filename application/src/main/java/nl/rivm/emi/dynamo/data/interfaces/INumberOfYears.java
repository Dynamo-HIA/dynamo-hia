package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface INumberOfYears {

	public abstract Integer getNumberOfYears();

	public abstract WritableValue getObservableNumberOfYears() throws DynamoConfigurationException;

	public abstract void setNumberOfYears(Integer numberOfYears);

}