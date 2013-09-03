package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IRefScenName {

	public abstract String getRefScenName();
	
	public abstract WritableValue getObservableRefScenName() throws DynamoConfigurationException;

	public abstract void setRefScenName(String popFileName);

}