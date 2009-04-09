package nl.rivm.emi.dynamo.data.interfaces;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

public interface ITabRiskFactorConfiguration {

	public String getName();

	public void setName(String name);

	public String getPrevalenceFileName();

	public void setPrevalenceFileName(String prevalenceFileName);

	public String getTransitionFileName();

	public void setTransitionFileName(String transitionFileName);
}