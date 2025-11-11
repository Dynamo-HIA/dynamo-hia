package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IResultType {

	public abstract String getResultType();

	@SuppressWarnings("rawtypes")
	public abstract WritableValue getObservableResultType() throws DynamoConfigurationException;

	public abstract void setResultType(String resultType);

}