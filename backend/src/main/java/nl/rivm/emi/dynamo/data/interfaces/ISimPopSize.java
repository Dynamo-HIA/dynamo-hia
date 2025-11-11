package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ISimPopSize {

	public abstract Integer getSimPopSize();

	@SuppressWarnings("rawtypes")
	public abstract WritableValue getObservableSimPopSize() throws DynamoConfigurationException;

	public abstract void setSimPopSize(Integer simPopSize);

}