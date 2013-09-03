package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IRandomSeed {

	public abstract Float getRandomSeed();

	public abstract WritableValue getObservableRandomSeed() throws DynamoConfigurationException;

	public abstract void setRandomSeed(Float randomSeed);

}