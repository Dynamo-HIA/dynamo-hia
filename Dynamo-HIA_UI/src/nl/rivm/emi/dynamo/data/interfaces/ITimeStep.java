package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ITimeStep {

	public abstract Float getTimeStep();

	public abstract WritableValue getObservableTimeStep();

	public abstract void setTimeStep(Float timeStep);

}