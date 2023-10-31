package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ITrend {

	public abstract Float getTrend() throws DynamoConfigurationException;

	public abstract WritableValue getObservableTrend() throws DynamoConfigurationException;

	public abstract void setTrend(Float trend) throws DynamoConfigurationException;

}