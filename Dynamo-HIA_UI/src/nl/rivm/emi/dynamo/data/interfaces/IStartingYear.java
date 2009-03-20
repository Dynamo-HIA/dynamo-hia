package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IStartingYear {

	public abstract Integer getStartingYear();

	public abstract WritableValue getObservableStartingYear();

	public abstract void setStartingYear(Integer startingYear);

}