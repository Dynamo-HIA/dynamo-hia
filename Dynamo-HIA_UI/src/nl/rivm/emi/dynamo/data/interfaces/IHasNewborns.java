package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IHasNewborns {

	public abstract boolean isHasNewborns();

	public abstract WritableValue getObservableHasNewborns();

	public abstract void setHasNewborns(boolean newborns);

}