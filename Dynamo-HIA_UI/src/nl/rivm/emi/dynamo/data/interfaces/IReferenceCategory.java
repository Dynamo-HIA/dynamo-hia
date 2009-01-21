package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IReferenceCategory {

	public Object putReferenceCategory(Integer index);

	public Integer getReferenceCategory();

	public WritableValue getObservableReferenceCategory();
}