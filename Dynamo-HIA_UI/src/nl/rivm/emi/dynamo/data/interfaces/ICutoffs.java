package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ICutoffs {

	public abstract Object putCutoff(Integer index, Float value);

	public abstract Float getCutoffValue(Integer index);

	public abstract WritableValue getObservableCutoffValue(Integer index);
	
	public abstract int getNumberOfCutoffs();
}