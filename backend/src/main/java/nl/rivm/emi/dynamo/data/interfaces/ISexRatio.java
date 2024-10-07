package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ISexRatio {

	public abstract Float getSexRatio();

	public abstract WritableValue getObservableSexRatio() throws DynamoConfigurationException;

	public abstract void setSexratio(Float sexRatio);

}