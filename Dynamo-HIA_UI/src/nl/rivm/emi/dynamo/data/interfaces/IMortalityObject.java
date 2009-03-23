package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IMortalityObject {

	public abstract int getNumberOfMortalities();

	public abstract Object putMortality(Integer count, String value);

	public abstract WritableValue getObservableUnit(int count);

}
