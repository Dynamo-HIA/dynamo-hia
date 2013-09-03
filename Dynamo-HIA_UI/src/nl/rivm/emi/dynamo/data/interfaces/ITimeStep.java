package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ITimeStep {

	public abstract Float getTimeStep();

	public abstract WritableValue getObservableTimeStep() throws DynamoConfigurationException;

	public abstract void setTimeStep(Float timeStep);

}