package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IRandomSeed {

	public abstract Float getRandomSeed();

	public abstract WritableValue getObservableRandomSeed();

	public abstract void setRandomSeed(Float randomSeed);

}