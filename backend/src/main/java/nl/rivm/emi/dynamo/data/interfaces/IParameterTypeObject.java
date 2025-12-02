package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IParameterTypeObject {

	Object putParameterType(String parameterType);

	String getParameterType();

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableParameterType() throws DynamoConfigurationException;

}
