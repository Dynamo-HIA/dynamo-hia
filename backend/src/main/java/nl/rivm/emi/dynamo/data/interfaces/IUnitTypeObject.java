package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IUnitTypeObject {

	Object putUnitType(String unitType);

	String getUnitType();

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableUnitType() throws DynamoConfigurationException;

}
