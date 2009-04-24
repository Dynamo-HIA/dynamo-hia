package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ISexRatio {

	public abstract Float getSexRatio();

	public abstract WritableValue getObservableSexRatio();

	public abstract void setSexratio(Float sexRatio);

}