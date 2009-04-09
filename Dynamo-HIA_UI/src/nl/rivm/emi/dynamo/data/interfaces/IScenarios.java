package nl.rivm.emi.dynamo.data.interfaces;

import java.util.Map;

public interface IScenarios {

	public abstract Map<String, ITabScenarioConfiguration> getScenarioConfigurations();

	public abstract void setScenarioConfigurations(
			Map<String, ITabScenarioConfiguration> scenarios);
}