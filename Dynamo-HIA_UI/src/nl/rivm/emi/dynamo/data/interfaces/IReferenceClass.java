package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IReferenceClass {

	public Object putReferenceClass(Integer index);

	public Integer getReferenceClass();

	public WritableValue getObservableReferenceClass();
}