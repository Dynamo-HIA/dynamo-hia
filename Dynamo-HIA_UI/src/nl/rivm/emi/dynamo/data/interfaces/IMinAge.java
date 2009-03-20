package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IMinAge {

	public abstract Integer getMinAge();

	public abstract WritableValue getObservableMinAge();

	public abstract void setMinAge(Integer minAge);

}