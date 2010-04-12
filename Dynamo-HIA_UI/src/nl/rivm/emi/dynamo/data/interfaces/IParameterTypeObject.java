package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IParameterTypeObject {

	Object putParameterType(String parameterType);

	String getParameterType();

	public WritableValue getObservableParameterType();

}
