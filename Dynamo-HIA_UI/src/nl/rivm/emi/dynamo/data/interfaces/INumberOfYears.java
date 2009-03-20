package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface INumberOfYears {

	public abstract Integer getNumberOfYears();

	public abstract WritableValue getObservableNumberOfYears();

	public abstract void setNumberOfYears(Integer numberOfYears);

}