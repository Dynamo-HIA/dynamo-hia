package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ISimPopSize {

	public abstract Integer getSimPopSize();

	public abstract WritableValue getObservableSimPopSize();

	public abstract void setSimPopSize(Integer simPopSize);

}