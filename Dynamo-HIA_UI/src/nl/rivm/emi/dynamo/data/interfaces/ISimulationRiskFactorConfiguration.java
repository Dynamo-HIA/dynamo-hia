package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ISimulationRiskFactorConfiguration {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract WritableValue getPrevalenceFileName();

	public abstract void setPrevalenceFileName(WritableValue prevalenceFileName);

	public abstract WritableValue getTransitionFileName();

	public abstract void setTransitionFileName(WritableValue transitionFileName);

	public abstract TypedHashMap<UniqueName> createTypedHashMap();

}