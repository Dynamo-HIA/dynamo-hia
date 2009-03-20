package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IResultType {

	public abstract String getResultType();

	public abstract WritableValue getObservableResultType();

	public abstract void setResultType(String resultType);

}