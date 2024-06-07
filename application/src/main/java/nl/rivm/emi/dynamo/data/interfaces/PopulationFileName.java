package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface PopulationFileName {

	public abstract String getPopulationFileName() throws DynamoConfigurationException;

	public abstract WritableValue getObservablePopulationFileName() throws DynamoConfigurationException;

	public abstract void setPopulationFileName(String populationFileName);

}