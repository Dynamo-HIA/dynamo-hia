package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IDistributionTypeObject {

	Object putDistributionType(String unitType);

	String getDistributionType();

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableDistributionType() throws DynamoConfigurationException;

}
