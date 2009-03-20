package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface PopulationFileName {

	public abstract String getPopulationFileName();

	public abstract WritableValue getObservablePopulationFileName();

	public abstract void setPopulationFileName(String populationFileName);

}