package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IHasNewborns {

	public abstract boolean isHasNewborns();

	@SuppressWarnings("rawtypes")
	public abstract WritableValue getObservableHasNewborns() throws DynamoConfigurationException;

	public abstract void setHasNewborns(boolean newborns);

}