package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.parts.ScenarioConfigurationData;

public interface IScenarios {

	public abstract TypedHashMap<ScenarioConfigurationData> getScenarios();

	public abstract void setScenarios(
			TypedHashMap<ScenarioConfigurationData> scenarios);
}