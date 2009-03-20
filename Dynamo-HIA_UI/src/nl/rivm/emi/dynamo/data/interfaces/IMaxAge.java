package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IMaxAge {

	public abstract Integer getMaxAge();

	public abstract WritableValue getObservableMaxAge();

	public abstract void setMaxAge(Integer maxAge);

}